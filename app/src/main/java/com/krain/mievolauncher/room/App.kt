package com.krain.mievolauncher.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class App(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "package") val `package`: String
)