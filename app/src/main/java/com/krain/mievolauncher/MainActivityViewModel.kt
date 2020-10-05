package com.krain.mievolauncher

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.krain.mievolauncher.recyclerview.SuggestionsAdapter
import com.krain.mievolauncher.room.App
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

    val suggestionsUpdateDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private lateinit var pm: PackageManager
    private lateinit var db: Db
    private lateinit var apps: AtomicReferenceArray<App>
    private var sequenceNumber = 0

    override fun onCleared() {
        super.onCleared()
        suggestionsUpdateDispatcher.close()
    }

    fun getSuggestions(seq: CharSequence): AtomicReferenceArray<App> =
        AtomicReferenceArray(
            db.appDao().getByName(seq.toString()).toTypedArray()
        )

    fun refreshApps() {
        viewModelScope.launch {
            val appDao = db.appDao()
            // check if database is empty
            // if yes, insert all installed applications
            if (appDao.getAll().isEmpty()) {
                appDao.putAll(pm.getInstalledApplications(PackageManager.GET_META_DATA).map {
                    App(it.name, it.packageName)
                })
            }
            // get changed packages
            // if not empty, check if changed are installed
            // if installed, insert into database
            // if uninstalled, delete from database
            val changed = pm.getChangedPackages(sequenceNumber++)
            if (changed != null) {
                val installed = mutableListOf<App>()
                val uninstalled = mutableListOf<String>()
                changed.packageNames.forEach {
                    try {
                        installed.add(
                            App(
                                pm.getPackageInfo(
                                    it,
                                    PackageManager.GET_META_DATA
                                ).applicationInfo.name,
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
            // get all installed applications from database
            apps = AtomicReferenceArray(appDao.getAll().toTypedArray())
        }
    }

    fun updateSuggestions(seq: CharSequence?) {
        if (seq.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            val suggestions = getSuggestions(seq)
            suggestionsAdapter.suggestions = suggestions
        }
    }

    private fun createDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            Db::class.java,
            "app-db"
        ).build()
    }
}