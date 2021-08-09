package com.krain.mievolauncher.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krain.mievolauncher.room.dao.AliasDao
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.dao.CommandDao
import com.krain.mievolauncher.room.dao.HistoryDao
import com.krain.mievolauncher.room.model.Alias
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.room.model.Command
import com.krain.mievolauncher.room.model.History
import com.krain.mievolauncher.room.typeconverter.CommandEnumConverter

@Database(entities = [App::class, Alias::class, History::class, Command::class], version = 1)
@TypeConverters(CommandEnumConverter::class)
abstract class Db : RoomDatabase() {
    abstract fun aliasDao() : AliasDao
    abstract fun appDao() : AppDao
    abstract fun histDao() : HistoryDao
    abstract fun commandDao() : CommandDao
}