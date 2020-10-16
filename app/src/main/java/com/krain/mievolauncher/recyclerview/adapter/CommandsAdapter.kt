package com.krain.mievolauncher.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.recyclerview.viewholder.CommandViewHolder
import com.krain.mievolauncher.room.model.Command
import java.util.concurrent.atomic.AtomicReferenceArray

class CommandsAdapter : RecyclerView.Adapter<CommandViewHolder>() {
    var suggestions = AtomicReferenceArray<Command>(0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return suggestions.length()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommandViewHolder.from(parent)

    override fun onBindViewHolder(holder: CommandViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }
}