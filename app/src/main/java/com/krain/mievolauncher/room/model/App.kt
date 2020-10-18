package com.krain.mievolauncher.room.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name", "count"])])
data class App(@PrimaryKey var pkg: String, var name: String, var count: Int)