package com.krain.mievolauncher

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.krain.mievolauncher.command.CommandEnum
import com.krain.mievolauncher.command.CommandFactory
import com.krain.mievolauncher.command.Executable
import com.krain.mievolauncher.mode.Mode
import com.krain.mievolauncher.mode.ModeManager
import com.krain.mievolauncher.room.dao.AliasDao
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.room.model.History
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.dao.HistoryDao
import com.krain.mievolauncher.room.dao.CommandDao
import com.krain.mievolauncher.util.DbService
import com.krain.mievolauncher.util.QueryParser
import com.krain.mievolauncher.util.PmService

class MainActivityViewModel : ViewModel() {
    var appContext: Context? = null
        set(value) {
            if (value == null) return
            field = value
            pm = PmService.getInstance(value).pm
            createDb(value)
        }
    val suggestionsAdapter by lazy { modeManager.appMode.appAdapter }
    val commandsAdapter by lazy { modeManager.appMode.cmdAdapter }
    val historyAdapter by lazy { modeManager.historyMode.adapter }

    private val vmDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val pkgFlags = PackageManager.GET_META_DATA
//    private var sequenceNumber = 0
    private var command: Executable? = null
    private lateinit var pm: PackageManager
    private lateinit var appDao: AppDao
    private lateinit var histDao: HistoryDao
    private lateinit var commandDao: CommandDao
//    private lateinit var aliasDao: AliasDao
    private lateinit var modeManager: ModeManager
    private lateinit var mode: Mode

    override fun onCleared() {
        super.onCleared()
        vmDispatcher.close()
    }

    suspend fun processQuery(seq: CharSequence?): Boolean {
        if (seq.isNullOrEmpty()) return false
        val args = QueryParser.parseArgs(seq)
        var cmd: CommandEnum = getCommandEnum(args[0]) ?: return false
        launchCommand(cmd, args.drop(1))
        return true
    }

    fun insertHistory(cmd: String) {
        viewModelScope.launch(vmDispatcher) {
            histDao.put(History(cmd))
        }
    }

    fun incrementUsage(pkg: String?) {
        if (pkg == null) return
        viewModelScope.launch(vmDispatcher) {
            val app = appDao.getByPkg(pkg)
            app.count++
            appDao.update(app)
        }
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

    private suspend fun getCommandEnum(name: String): CommandEnum? {
        var cmd: CommandEnum? = null
        viewModelScope.async(vmDispatcher) {
            cmd = commandDao.getFirstByExactName(name)?.type
        }.join()
        return cmd
    }

    private fun getInstalled() = pm.queryIntentActivities(
        Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
        pkgFlags
    ).map {
        App(
            it.activityInfo.packageName,
            pm.getApplicationLabel(it.activityInfo.applicationInfo).toString(),
            0
        )
    }

    private fun getAppsFromDb() = appDao.getAll()

    // Updates the list of packages in the database
    // NOTE: in future, could update apps after :uninstall
    private fun updateInstalled() {
        val old = getAppsFromDb()
        val new = getInstalled()
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size
            // Just identifies if app is still installed
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition].pkg == new[newItemPosition].pkg
            }
            // Allows updates based on name changes
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition].name == new[newItemPosition].name
            }
        }
        val diff = DiffUtil.calculateDiff(diffCallback)
        val uninstalled = mutableListOf<String>()
        val installedOrUpdated = mutableListOf<App>()
        // Track which indices we've already checked in the new list
//        val seen = mutableListOf<Int>()
        for(i in 0..old.lastIndex) {
            val newPos = diff.convertOldPositionToNew(i)
            // If not present in new list -> delete
            if(newPos == DiffUtil.DiffResult.NO_POSITION) {
                uninstalled.add(old[i].pkg)
            } else {
                val updated = new[newPos]
                updated.count = old[i].count
//                val alias = aliasDao.getByPkg(updated.pkg)?.name
//                if(alias !== null) {
//                    updated.name = alias
//                }
                installedOrUpdated.add(updated)
//                seen.add(newPos)
            }
        }
        // Every index that hasn't been visited points to a new app
//        val seenTracker = seen.listIterator()
        for(i in 0..new.lastIndex) {
//            if(seenTracker.hasNext() &&
//                i === seenTracker.next()) continue
//            else
            val oldPos = diff.convertNewPositionToOld(i)
            // If not present in old list -> add
            if(oldPos == DiffUtil.DiffResult.NO_POSITION) {
                installedOrUpdated.add(new[i])
            }
        }
        appDao.putAndUpdateAll(installedOrUpdated)
        appDao.deleteAllByPkgs(uninstalled)
    }

    private fun createDb(context: Context) {
        viewModelScope.launch(vmDispatcher) {
            val db = DbService.getInstance(context).db
            appDao = db.appDao()
            histDao = db.histDao()
            commandDao = db.commandDao()
//            aliasDao = db.commandDao()
            initMode()
        }
    }

    private fun initMode() {
        modeManager = ModeManager.getInstance()
        mode = modeManager.mode
    }

    private fun launchCommand(cmd: CommandEnum, args: List<String>) {
        viewModelScope.launch(vmDispatcher) {
            if (cmd == CommandEnum.UNDO) {
                command?.undo()
                command = null
                return@launch
            }
            command = CommandFactory.getInstance(cmd)
            command!!.execute(args = args.toTypedArray())
        }
    }
}