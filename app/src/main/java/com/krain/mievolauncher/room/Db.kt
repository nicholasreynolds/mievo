package com.krain.mievolauncher.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.dao.HistoryDao
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.room.model.History

@Database(entities = [App::class, History::class], version = 1)
abstract class Db : RoomDatabase() {
    abstract fun appDao() : AppDao
    abstract fun histDao() : HistoryDao
}