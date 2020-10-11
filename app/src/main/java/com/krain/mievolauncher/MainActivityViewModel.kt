package com.krain.mievolauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.krain.mievolauncher.mode.AppMode
import com.krain.mievolauncher.mode.HistoryMode
import com.krain.mievolauncher.mode.Mode
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.room.model.History
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.dao.HistoryDao
import com.krain.mievolauncher.room.Db
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class MainActivityViewModel : ViewModel() {
    var appContext: Context? = null
        set(value) {
            if (value == null) {
                return
            }
            field = value
            pm = value.packageManager
            viewModelScope.launch { createDb(value) }
        }
    var showHistory: Boolean = false
        set(value) {
            field = value
            mode = if (value) historyMode else appMode
        }
    val suggestionsAdapter by lazy { appMode.adapter }
    val historyAdapter by lazy { historyMode.adapter }

    private val vmDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val pkgFlags = PackageManager.GET_META_DATA
    private var sequenceNumber = 0
    private lateinit var pm: PackageManager
    private lateinit var appDao: AppDao
    private lateinit var histDao: HistoryDao
    private lateinit var historyMode : HistoryMode
    private lateinit var appMode : AppMode
    private lateinit var mode : Mode

    override fun onCleared() {
        super.onCleared()
        vmDispatcher.close()
    }

    fun updateSuggestions(seq: CharSequence?) {
        viewModelScope.launch(vmDispatcher) {
            mode.updateSuggestions(seq)
        }
    }

    fun refreshApps() {
        viewModelScope.launch(vmDispatcher) {
            updateInstalled()
//            updateChangedPkgs()
        }
    }

    fun insertHistory(cmd: String) {
        viewModelScope.launch(vmDispatcher) {
            histDao.put(History(cmd))
        }
    }

    //  check if database is empty
    //      if yes, insert all installed applications
    private suspend fun updateInstalled() {
        if (appDao.getAll().isEmpty()) {
            appDao.putAll(
                pm.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    pkgFlags
                ).map {
                    App(
                        pm.getApplicationLabel(it.activityInfo.applicationInfo).toString(),
                        it.activityInfo.packageName
                    )
                }
            )
        }
    }

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
                            it
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

    private fun initMode() {
        historyMode = HistoryMode(histDao)
        appMode = AppMode(appDao)
        mode = appMode
    }

    private fun createDb(context: Context) {
        val db = Room.databaseBuilder(
            context,
            Db::class.java,
            "app-db"
        ).build()
        appDao = db.appDao()
        histDao = db.histDao()
        initMode()
    }
}