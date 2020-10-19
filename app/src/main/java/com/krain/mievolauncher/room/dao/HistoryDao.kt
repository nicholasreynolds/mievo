package com.krain.mievolauncher.room.dao

import androidx.room.*
import com.krain.mievolauncher.room.model.History

@Dao
interface HistoryDao {
    @Query("select * from history")
    fun getAll(): List<History>

    @Query("select * from history where description like '%' || (:desc) || '%' order by description ASC")
    fun getByDesc(desc: String): List<History>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun put(hist: History)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun putAll(hists: List<History>)

    @Delete
    fun delete(hist: History)
}