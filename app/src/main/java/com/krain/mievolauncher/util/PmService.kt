package com.krain.mievolauncher.util

import android.content.Context
import android.content.pm.PackageManager

class PmService private constructor(context: Context) {
    companion object {
        private var instance : PmService? = null
        fun getInstance(context: Context) : PmService {
            if(instance == null) {
                instance = PmService(context)
            }
            return instance!!
        }
    }

    val pm: PackageManager = context.packageManager
}