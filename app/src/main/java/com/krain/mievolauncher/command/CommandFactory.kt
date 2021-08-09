package com.krain.mievolauncher.command

class CommandFactory {
    companion object {
        fun getInstance(`enum`: CommandEnum) : Executable? = when(`enum`) {
            CommandEnum.ALIAS -> AliasCmd()
            CommandEnum.UNALIAS -> UnAliasCmd()
            CommandEnum.UNDO -> null
        }
    }
}