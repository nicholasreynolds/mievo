package com.krain.mievolauncher.util

import android.content.Context
import androidx.room.Room
import com.krain.mievolauncher.command.CommandEnum
import com.krain.mievolauncher.room.Db
import com.krain.mievolauncher.room.model.Command

class DbService private constructor(context: Context) {
    companion object {
        private var instance: DbService? = null
        fun getInstance(context: Context): DbService {
            if (instance == null) {
                instance = DbService(context)
            }
            return instance!!
        }
        fun getInstance() : DbService? {
            return instance
        }
    }

    val db: Db = Room.databaseBuilder(
        context,
        Db::class.java,
        "app-db"
    ).build()

    init {
        loadCommands()
    }

    private fun loadCommands() {
        db.commandDao().putAll(
            CommandEnum.values().map {
                Command(it.cmd, it)
            }
        )
    }
}