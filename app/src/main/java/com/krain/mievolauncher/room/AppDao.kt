package com.krain.mievolauncher.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppDao {
    @Query("select * from app")
    fun getAll() : List<App>

    @Query("select * from app where name like '(:name)%' limit 1")
    fun getByName(name: String) : App

    @Query("select * from app where package like '(:package)%' limit 1")
    fun getByPkg(`package`: String) : App

    @Insert
    fun put(app: App)

    @Insert
    fun putAll(apps: List<App>)

    @Delete
    fun delete(app: App)
}