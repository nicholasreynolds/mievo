package com.krain.mievolauncher.room.dao

import androidx.room.*
import com.krain.mievolauncher.room.model.Alias

@Dao
interface AliasDao {
    @Query("select * from alias")
    fun getAll() : List<Alias>

    @Query("select * from alias where name like '%' || (:name) || '%' order by name")
    fun getByName(name: String) : List<Alias>

    @Query("select * from alias where pkg like '%' || (:pkg) || '%' limit 1")
    fun getByPkg(pkg: String) : Alias

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putOrUpdate(alias: Alias)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun putOrUpdateAll(aliases: List<Alias>)

    @Delete
    fun delete(app: Alias)

    @Query("delete from app where pkg in (:packages)")
    fun deleteAllByPkgs(packages: List<String>)
}