package com.krain.mievolauncher.room

import androidx.room.*

@Dao
interface AppDao {
    @Query("select * from app")
    fun getAll() : List<App>

    @Query("select * from app where name like (:name) || '%'")
    fun getByName(name: String) : List<App>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(app: App)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putAll(apps: List<App>)

    @Delete
    suspend fun delete(app: App)

    @Query("delete from app where pkg in (:packages)")
    suspend fun deleteAllByPkgs(packages: List<String>)
}