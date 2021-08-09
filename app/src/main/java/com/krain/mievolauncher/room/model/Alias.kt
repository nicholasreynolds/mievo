package com.krain.mievolauncher.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alias(@PrimaryKey var pkg: String, var name: String, var prev: String)