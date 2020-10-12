package com.krain.mievolauncher.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class App(@PrimaryKey var pkg: String, var name: String, var count: Int)