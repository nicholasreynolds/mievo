package com.krain.mievolauncher.command

import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.util.DbService

class RenameCmd : Executable() {
    private val db = DbService.getInstance()?.db
    private var op: Array<String>? = null

    override val enum = CommandEnum.RENAME

    override fun execute(vararg args: String) {
        if(args.size != 2) return
        rename(args[0], args[1])
    }

    override fun undo() {
        if(op == null) return
        rename(op!![1], op!![0])
    }

    private fun rename(appName: String, newName: String) {
        if(db == null) return
        var apps: List<App>
        db.appDao().apply {
            apps = getByName(appName)
            if(apps.isEmpty()) return

            val app = apps[0]
            app.name = newName
            update(app)

            apps = getAll()
        }
        op = arrayOf(appName, newName)
    }
}