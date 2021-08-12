package com.krain.mievolauncher.command;

import com.krain.mievolauncher.room.model.Alias
import com.krain.mievolauncher.room.model.App;
import com.krain.mievolauncher.util.DbService;

class UnAliasCmd : Executable() {
    private val db = DbService.getInstance()?.db
    private var op: Pair<App, Alias>? = null


    override val enum = CommandEnum.ALIAS

    override fun execute(vararg args: String) {
        if(args.size != 1) return
        unalias(args[0])
    }

    override fun undo() {
        if(op == null || db == null) return
        val app = db.appDao().getByPkg(op!!.first.pkg)
        val alias = op!!.second
        app.name = alias.name
        db.appDao().update(app)
        db.aliasDao().putOrUpdate(alias)
    }

    private fun unalias(appName: String) {
        if(db == null) return
        val apps = db.appDao().getByName(appName)
        if(apps.isEmpty()) return

        val app = apps[0]
        val alias = db.aliasDao().getByPkg(app.pkg)
        app.name = alias.prev
        db.appDao().update(app)
        db.aliasDao().delete(alias)

        op = Pair(app, alias)
    }
}
