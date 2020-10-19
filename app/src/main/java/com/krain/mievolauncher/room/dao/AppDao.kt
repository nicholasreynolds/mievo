package com.krain.mievolauncher.room.dao

import androidx.room.*
import com.krain.mievolauncher.room.model.App

@Dao
interface AppDao {
    @Query("select * from app")
    fun getAll() : List<App>

    @Query("select * from app where name like '%' || (:name) || '%' order by count desc, name")
    fun getByName(name: String) : List<App>

    @Query("select * from app where pkg like '%' || (:pkg) || '%' limit 1")
    fun getByPkg(pkg: String) : App

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun put(app: App)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun putAll(apps: List<App>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(app: App)

    @Delete
    fun delete(app: App)

    @Query("delete from app where pkg in (:packages)")
    fun deleteAllByPkgs(packages: List<String>)
}