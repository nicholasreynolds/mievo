package com.krain.mievolauncher.command

class CommandFactory {
    companion object {
        fun getInstance(`enum`: CommandEnum) : Executable = when(`enum`) {
            CommandEnum.RENAME -> RenameCmd()
        }
    }
}