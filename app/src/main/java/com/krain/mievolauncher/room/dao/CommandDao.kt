package com.krain.mievolauncher.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krain.mievolauncher.room.model.Command

@Dao
interface CommandDao {
    @Query("select * from command")
    fun getAll() : List<Command>

    @Query("select * from command where name like '%' || (:name) || '%'")
    fun getByName(name: String) : List<Command>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun putAll(commands: List<Command>)
}