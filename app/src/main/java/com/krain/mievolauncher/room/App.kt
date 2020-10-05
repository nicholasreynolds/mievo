package com.krain.mievolauncher.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class App(
    @PrimaryKey var name: String,
    var pkg: String
)