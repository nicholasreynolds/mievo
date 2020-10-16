package com.krain.mievolauncher.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.krain.mievolauncher.command.CommandEnum

@Entity
data class Command(
    @PrimaryKey
    var name: String,   // command as it will be typed
    var type: CommandEnum    // json string serialized command object
)