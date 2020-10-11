package com.krain.mievolauncher.mode

import com.krain.mievolauncher.recyclerview.adapter.SuggestionsAdapter
import com.krain.mievolauncher.room.dao.AppDao
import com.krain.mievolauncher.room.model.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReferenceArray

class AppMode(private val dao: AppDao) : Mode {
    val adapter = SuggestionsAdapter()

    override suspend fun updateSuggestions(seq: CharSequence?) {
        val suggestions = AtomicReferenceArray(getSuggestions(seq).toTypedArray())
        withContext(Dispatchers.Main) { // Run on UI thread
            adapter.suggestions = suggestions
        }
    }

    private fun getSuggestions(seq: CharSequence?): List<App> = when {
        seq.isNullOrEmpty() -> emptyList()
        else -> dao.getByName(seq.toString())
    }
}