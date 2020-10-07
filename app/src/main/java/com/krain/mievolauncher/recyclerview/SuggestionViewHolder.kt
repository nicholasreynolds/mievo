package com.krain.mievolauncher.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.MainActivity
import com.krain.mievolauncher.R
import com.krain.mievolauncher.room.App

class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(parent: ViewGroup) =
            SuggestionViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.suggestion,
                        parent,
                        false
                    )
            )
    }

    fun bind(item: App) {
        val view = itemView.findViewById<TextView>(R.id.suggestion)
        view.text = item.name
        // Launch app
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
                // Get launch intent for package, then launch
                (itemView.context as MainActivity).launchApp(
                    itemView.context.packageManager
                        .getLaunchIntentForPackage(item.pkg)
                        ?.addCategory(Intent.CATEGORY_LAUNCHER)
                        ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            true
        }
    }
}