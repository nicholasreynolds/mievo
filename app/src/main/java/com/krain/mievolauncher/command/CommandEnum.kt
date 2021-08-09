package com.krain.mievolauncher.command

import kotlinx.serialization.Serializable

@Serializable
enum class CommandEnum(val cmd: String) {
    ALIAS("alias"),
    UNALIAS("unalias"),
    UNDO("undo")
}