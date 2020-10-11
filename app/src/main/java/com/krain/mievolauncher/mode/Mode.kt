package com.krain.mievolauncher.mode

interface Mode {
    suspend fun updateSuggestions(seq: CharSequence?)
}