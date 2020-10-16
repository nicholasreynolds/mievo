package com.krain.mievolauncher.mode

import com.krain.mievolauncher.recyclerview.adapter.CommandsAdapter
import com.krain.mievolauncher.recyclerview.adapter.SuggestionsAdapter
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.dao.CommandDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReferenceArray

class AppMode(private val appDao: AppDao, private val cmdDao: CommandDao) : Mode {
    val appAdapter = SuggestionsAdapter()
    val cmdAdapter = CommandsAdapter()

    override suspend fun updateSuggestions(seq: CharSequence?) {
        val appSuggestions = getSuggestions(seq, appDao::getByName)
        val cmdSuggestions = getSuggestions(seq, cmdDao::getByName)
        withContext(Dispatchers.Main) { // Run on UI thread
            appAdapter.suggestions = appSuggestions
            cmdAdapter.suggestions = cmdSuggestions
        }
    }

    private inline fun <reified T> getSuggestions(
        seq: CharSequence?,
        f: (String) -> (List<T>)
    ): AtomicReferenceArray<T> = when {
        seq.isNullOrEmpty() -> AtomicReferenceArray<T>(0)
        else -> AtomicReferenceArray(f(seq.toString()).toTypedArray())
    }
}