package com.krain.mievolauncher.recyclerview

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.R
import com.krain.mievolauncher.room.App
import java.util.concurrent.atomic.AtomicReferenceArray

class SuggestionsAdapter : RecyclerView.Adapter<SuggestionViewHolder>() {

    lateinit var suggestions: AtomicReferenceArray<App>

    override fun getItemCount(): Int = suggestions.length()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder =
        SuggestionViewHolder.from(parent)

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }
}