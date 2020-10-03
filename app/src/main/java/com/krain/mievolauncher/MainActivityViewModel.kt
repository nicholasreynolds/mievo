package com.krain.mievolauncher

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.RoomDatabase

class MainActivityViewModel : ViewModel() {
    var pm: PackageManager? = null
    var db: RoomDatabase? = null
    var suggestions = MutableLiveData<List<String>>()

    private lateinit var apps : List<ApplicationInfo>

    fun refreshApps() {
        if (pm == null) {
            return
        }
        apps = pm!!.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    fun updateSuggestions(seq: CharSequence?) {
        if (seq == null) {
            return
        }
        // determine suggestions
        // update recycler
    }
}