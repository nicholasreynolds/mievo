package com.krain.mievolauncher.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.MainActivity
import com.krain.mievolauncher.R
import com.krain.mievolauncher.room.model.History

class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(parent: ViewGroup) =
            HistoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.suggestion,
                        parent,
                        false
                    )
            )
    }

    fun bind(item: History) {
        val view = itemView.findViewById<TextView>(R.id.suggestion)
        view.text = item.description
        // Launch app
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
                // Get launch intent for package, then launch
                with(itemView.context as MainActivity) {
                    setQuery(item.description)
                    toggleHistory()
                }
            }
            return@setOnTouchListener true
        }
    }
}