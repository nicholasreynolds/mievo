package com.krain.mievolauncher.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.MainActivity
import com.krain.mievolauncher.R
import com.krain.mievolauncher.room.model.Command

class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(parent: ViewGroup) =
            CommandViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.suggestion_command,
                        parent,
                        false
                    )
            )
    }

    fun bind(item: Command) {
        val view = itemView.findViewById<TextView>(R.id.suggestion)
        view.text = item.name
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
                (itemView.context as MainActivity).setCommand(item)
            }
            return@setOnTouchListener true
        }
    }
}