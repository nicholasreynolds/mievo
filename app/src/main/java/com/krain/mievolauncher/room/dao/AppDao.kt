package com.krain.mievolauncher.room.dao

import androidx.room.*
import com.krain.mievolauncher.room.model.App

@Dao
interface AppDao {
    @Query("select * from app")
    fun getAll() : List<App>

    @Query("select * from app where name like '%' || (:name) || '%' order by name ASC")
    fun getByName(name: String) : List<App>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun put(app: App)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun putAll(apps: List<App>)

    @Delete
    suspend fun delete(app: App)

    @Query("delete from app where pkg in (:packages)")
    suspend fun deleteAllByPkgs(packages: List<String>)
}