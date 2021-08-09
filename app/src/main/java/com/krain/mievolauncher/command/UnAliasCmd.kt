package com.krain.mievolauncher.command;

import com.krain.mievolauncher.room.model.Alias
import com.krain.mievolauncher.room.model.App;
import com.krain.mievolauncher.util.DbService;

class UnAliasCmd : Executable() {
    private val db = DbService.getInstance()?.db
    private var op: Pair<App, Alias>? = null


    override val enum = CommandEnum.UNALIAS

    override fun execute(vararg args: String) {
        if(args.size != 2) return
        unalias(args[0], args[1])
    }

    override fun undo() {
        if(op == null || db == null) return
        val app = op!!.first
        app.name = op!!.second.prev
        db.appDao().update(app)
        db.aliasDao().deleteAllByPkgs(listOf(app.pkg))
    }

    private fun unalias(appName: String, newName: String) {
        if(db == null) return
        with(db.appDao()) {
            val apps = getByName(appName)
            if(apps.isEmpty()) return

            val app = apps[0]
            val alias = Alias(app.pkg, newName, app.name)
            db.aliasDao().putOrUpdate(alias)
            app.name = alias.name
            update(app)

            op = Pair(app, alias)
        }
    }
}
