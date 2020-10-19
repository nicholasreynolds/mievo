package com.krain.mievolauncher

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krain.mievolauncher.command.Executable
import com.krain.mievolauncher.mode.Mode
import com.krain.mievolauncher.mode.ModeManager
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.room.model.History
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.dao.HistoryDao
import com.krain.mievolauncher.room.dao.CommandDao
import com.krain.mievolauncher.util.DbService
import com.krain.mievolauncher.util.PmService

class MainActivityViewModel : ViewModel() {
    var appContext: Context? = null
        set(value) {
            if (value == null) return
            field = value
            pm = PmService.getInstance(value).pm
            createDb(value)
        }
    var command: Executable? = null
    val suggestionsAdapter by lazy { modeManager.appMode.appAdapter }
    val commandsAdapter by lazy { modeManager.appMode.cmdAdapter }
    val historyAdapter by lazy { modeManager.historyMode.adapter }

    private val vmDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val pkgFlags = PackageManager.GET_META_DATA
    private var sequenceNumber = 0
    private lateinit var pm: PackageManager
    private lateinit var appDao: AppDao
    private lateinit var histDao: HistoryDao
    private lateinit var commandDao: CommandDao
    private lateinit var modeManager: ModeManager
    private lateinit var mode: Mode

    override fun onCleared() {
        super.onCleared()
        vmDispatcher.close()
    }

    fun insertHistory(cmd: String) {
        viewModelScope.launch(vmDispatcher) {
            histDao.put(History(cmd))
        }
    }

    fun incrementUsage(pkg: String?) {
        if (pkg == null) {
            return
        }
        viewModelScope.launch(vmDispatcher) {
            val app = appDao.getByPkg(pkg)
            app.count++
            appDao.update(app)
        }
    }

    fun processQuery(seq: CharSequence?): Boolean {
        if (seq.isNullOrEmpty() || command == null) {
            return false
        }
        val args = parseArgs(seq)
        if (args[0] != command?.enum?.cmd) return false
        viewModelScope.launch(vmDispatcher) {
            command!!.execute(args = args.drop(1).toTypedArray())
        }
        return true
    }

    fun refreshApps() {
        viewModelScope.launch(vmDispatcher) {
            updateInstalled()
//            updateChangedPkgs()
        }
    }

    fun switchMode() {
        mode = modeManager.switchMode()
    }

    fun updateSuggestions(seq: CharSequence?) {
        viewModelScope.launch(vmDispatcher) {
            mode.updateSuggestions(seq?.trim())
        }
    }

    //  check if database is empty
    //      if yes, insert all installed applications
    private suspend fun updateInstalled() = appDao.putAll(
        pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
            pkgFlags
        ).map {
            App(
                it.activityInfo.packageName,
                pm.getApplicationLabel(it.activityInfo.applicationInfo).toString(),
                0
            )
        }
    )

    //  get changed packages
    //      if not empty, check if changed are installed
    //          if installed, insert into database
    //          if uninstalled, delete from database
    // NOTE: Not currently in use, but later could update apps after :uninstall
    private suspend fun updateChangedPkgs() {
        val changed = pm.getChangedPackages(sequenceNumber++)
        if (changed != null) {
            val installed = mutableListOf<App>()
            val uninstalled = mutableListOf<String>()
            changed.packageNames.forEach {
                try {
                    installed.add(
                        App(
                            pm.getApplicationLabel(
                                pm.getPackageInfo(
                                    it,
                                    PackageManager.GET_META_DATA
                                ).applicationInfo
                            ).toString(),
                            it,
                            0
                        )
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    uninstalled.add(it)
                }
            }
            appDao.putAll(installed)
            appDao.deleteAllByPkgs(uninstalled)
        }
    }

    private fun createDb(context: Context) {
        viewModelScope.launch(vmDispatcher) {
            val db = DbService.getInstance(context).db
            appDao = db.appDao()
            histDao = db.histDao()
            commandDao = db.commandDao()
            initMode()
        }
    }

    private fun initMode() {
        modeManager = ModeManager.getInstance()
        mode = modeManager.mode
    }

    private fun parseArgs(seq: CharSequence): List<String> {
        return seq.split(' ')
    }
}