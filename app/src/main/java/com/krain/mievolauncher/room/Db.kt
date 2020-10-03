package com.krain.mievolauncher.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [App::class], version = 1)
abstract class Db : RoomDatabase() {
    abstract fun appDao() : AppDao
}