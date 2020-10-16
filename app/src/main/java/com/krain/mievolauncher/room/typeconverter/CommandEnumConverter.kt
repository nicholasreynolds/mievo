package com.krain.mievolauncher.room.typeconverter

import androidx.room.TypeConverter
import com.krain.mievolauncher.command.CommandEnum
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CommandEnumConverter {
    @TypeConverter
    fun decodeFromString(json: String) : CommandEnum {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun encodeToString(command: CommandEnum) : String {
        return Json.encodeToString(command)
    }
}