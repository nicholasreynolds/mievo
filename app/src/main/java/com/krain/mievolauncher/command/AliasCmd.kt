package com.krain.mievolauncher.command

import com.krain.mievolauncher.room.model.Alias
import com.krain.mievolauncher.room.model.App
import com.krain.mievolauncher.util.DbService

class AliasCmd : Executable() {
    private val db = DbService.getInstance()?.db
    private var op: Pair<App, Alias>? = null

    override val enum = CommandEnum.ALIAS

    override fun execute(vararg args: String) {
        if(args.size != 2) return
        alias(args[0], args[1])
    }

    override fun undo() {
        if(op == null || db == null) return
        val app = db.appDao().getByPkg(op!!.first.pkg)
        val alias = op!!.second
        app.name = alias.prev
        db.appDao().update(app)
        db.aliasDao().delete(alias)
    }

    private fun alias(appName: String, newName: String) {
        if(db == null) return
        with(db.appDao()) {
            val apps = getByName(appName)
            if(apps.isEmpty()) return

            val app = apps[0]
            var alias = db.aliasDao().getByPkg(app.pkg)
            if(alias === null) {
                alias = Alias(app.pkg, newName, app.name)
            } else {
                alias.name = newName
            }
            db.aliasDao().putOrUpdate(alias)
            app.name = alias.name
            update(app)

            op = Pair(app, alias)
        }
    }
}