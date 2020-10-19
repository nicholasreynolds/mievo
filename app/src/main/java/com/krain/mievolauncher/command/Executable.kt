package com.krain.mievolauncher.command

abstract class Executable {
    abstract val enum: CommandEnum
    // Performs bounds check to ensure correct number of parameters,
    // then executes definition.
    // Throws IllegalArgumentException if incorrect number of arguments or
    // arguments of incorrect type are passed
    abstract fun execute(vararg args: String)
    abstract fun undo()
}