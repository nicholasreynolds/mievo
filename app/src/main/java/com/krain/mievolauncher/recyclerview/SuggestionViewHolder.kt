package com.krain.mievolauncher.recyclerview

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        try {
            // Create launch intent for package
            val intent: Intent? = itemView.context.packageManager
                .getLaunchIntentForPackage(item.pkg)
                ?.addCategory(Intent.CATEGORY_LAUNCHER)
                ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // Launch app
            view.setOnClickListener {
                itemView.context.startActivity(intent)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("LAUNCH_ERROR", "Could not find package ${item.pkg}")
        }
    }
}