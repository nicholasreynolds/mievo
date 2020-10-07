package com.krain.mievolauncher.recyclerview

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.MainActivity
import com.krain.mievolauncher.R
import com.krain.mievolauncher.room.App

class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {
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

    private lateinit var intent: Intent

    fun bind(item: App) {
        val view = itemView.findViewById<TextView>(R.id.suggestion)
        view.text = item.name
        try {
            // Create launch intent for package
            intent = itemView.context.packageManager
                .getLaunchIntentForPackage(item.pkg)!!
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // Launch app
            view.setOnTouchListener(this)
        } catch (e: NullPointerException) {
            Log.e("LAUNCH_ERROR", "Could not find package ${item.pkg}")
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        if(event.action == MotionEvent.ACTION_UP) {
            (itemView.context as MainActivity).launchApp(intent)
        }
        return true
    }
}