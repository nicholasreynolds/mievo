package com.krain.mievolauncher

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.krain.mievolauncher.recyclerview.SuggestionsAdapter
import com.krain.mievolauncher.room.App
import com.krain.mievolauncher.room.AppDao
import com.krain.mievolauncher.room.Db
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReferenceArray

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
    lateinit var suggestionsAdapter: SuggestionsAdapter

    private var sequenceNumber = 0
    private val vmDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val pkgFlags = PackageManager.GET_META_DATA
    private lateinit var pm: PackageManager
    private lateinit var appDao: AppDao
    private lateinit var apps: AtomicReferenceArray<App>

    override fun onCleared() {
        super.onCleared()
        vmDispatcher.close()
    }

    fun updateSuggestions(seq: CharSequence?) {
        viewModelScope.launch(vmDispatcher) {
            val suggestions = getSuggestions(seq)
            withContext(viewModelScope.coroutineContext) {
                suggestionsAdapter.suggestions = suggestions
            }
        }
    }

    fun refreshApps() {
        viewModelScope.launch(vmDispatcher) {
            updateInstalled()
//            updateChangedPkgs()
            // get all installed applications from database
            apps = AtomicReferenceArray(appDao.getAll().toTypedArray())
        }
    }

    //  check if database is empty
    //      if yes, insert all installed applications
    private suspend fun updateInstalled() {
        if (appDao.getAll().isEmpty()) {
            appDao.putAll(pm.getInstalledApplications(pkgFlags).map {
                App(pm.getApplicationLabel(it).toString(), it.packageName)
            })
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

    private fun getSuggestions(seq: CharSequence?): AtomicReferenceArray<App> = when {
        seq.isNullOrEmpty() -> AtomicReferenceArray<App>(0)
        else -> AtomicReferenceArray(
            appDao.getByName(seq.toString()).toTypedArray()
        )
    }

    private fun createDb(context: Context) {
        appDao = Room.databaseBuilder(
            context,
            Db::class.java,
            "app-db"
        ).build()
            .appDao()
    }
}