package com.krain.mievolauncher.mode

import com.krain.mievolauncher.recyclerview.adapter.HistoryAdapter
import com.krain.mievolauncher.room.dao.HistoryDao
import com.krain.mievolauncher.room.model.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReferenceArray

class HistoryMode(private val dao: HistoryDao) : Mode {
    val adapter = HistoryAdapter()

    override suspend fun updateSuggestions(seq: CharSequence?) {
        val suggestions = AtomicReferenceArray(getSuggestions(seq).toTypedArray())
        withContext(Dispatchers.Main) { // Run on UI thread
            adapter.suggestions = suggestions
        }
    }

    private fun getSuggestions(seq: CharSequence?): List<History> = when {
        seq.isNullOrEmpty() -> dao.getAll()
        else -> dao.getByDesc(seq.toString())
    }
}