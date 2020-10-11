package com.krain.mievolauncher.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(@PrimaryKey val description: String)