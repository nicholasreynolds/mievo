package com.krain.mievolauncher.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

sealed class LauncherEntity
@Entity
data class History(@PrimaryKey val description: String): LauncherEntity()
@Entity
data class App(@PrimaryKey var name: String, var pkg: String): LauncherEntity()