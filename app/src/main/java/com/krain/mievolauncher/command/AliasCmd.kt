package com.krain.mievolauncher.command

import com.krain.mievolauncher.room.model.Alias
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.util.DbService

class AliasCmd : Executable() {
    private val db = DbService.getInstance()?.db
    private var op: Array<String>? = null

    override val enum = CommandEnum.ALIAS

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
        with(db.appDao()) {
            apps = getByName(appName)
            if(apps.isEmpty()) return

            val app = apps[0]
            val alias = Alias(app.pkg, newName, app.name)
            db.aliasDao().putOrUpdate(alias)
            app.name = alias.name
            update(app)

            apps = getAll()
        }
        op = arrayOf(appName, newName)
    }
}