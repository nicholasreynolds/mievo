package com.krain.mievolauncher.command

class RenameCmd : Executable() {
    override val name: String = "rename"

    override fun execute(vararg args: String) {
        TODO("Not yet implemented")
    }

    override fun undo() {
        TODO("Not yet implemented")
    }

    override fun toEnum(): CommandEnum {
        return CommandEnum.RENAME
    }
}