package com.krain.mievolauncher.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.recyclerview.viewholder.SuggestionViewHolder
import com.krain.mievolauncher.room.model.App
import java.util.concurrent.atomic.AtomicReferenceArray

class SuggestionsAdapter : RecyclerView.Adapter<SuggestionViewHolder>() {

    var suggestions = AtomicReferenceArray<App>(0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = suggestions.length()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder =
        SuggestionViewHolder.from(parent)

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }
}