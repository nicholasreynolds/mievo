package com.krain.mievolauncher.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.recyclerview.viewholder.HistoryViewHolder
import com.krain.mievolauncher.room.model.History
import java.util.concurrent.atomic.AtomicReferenceArray

class HistoryAdapter : RecyclerView.Adapter<HistoryViewHolder>() {

    var suggestions = AtomicReferenceArray<History>(0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = suggestions.length()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder.from(parent)

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }
}