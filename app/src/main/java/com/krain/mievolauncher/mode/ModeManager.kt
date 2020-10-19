package com.krain.mievolauncher.mode

import com.krain.mievolauncher.room.Db
import com.krain.mievolauncher.util.DbService

class ModeManager private constructor() {
    companion object {
        private var instance: ModeManager? = null
        fun getInstance() : ModeManager {
            if(instance == null) {
                instance = ModeManager()
            }
            return instance!!
        }
    }

    var mode: Mode
        private set
    val appMode: AppMode
    val historyMode: HistoryMode

    private var historyShown = false

    init {
        val db: Db = DbService.getInstance()!!.db // non-null since db created earlier by vm
        appMode = AppMode(db.appDao(), db.commandDao())
        historyMode = HistoryMode(db.histDao())
        mode = appMode
    }

    fun switchMode() : Mode {
        mode = if(historyShown) appMode else historyMode
        historyShown = !historyShown
        return mode
    }
}