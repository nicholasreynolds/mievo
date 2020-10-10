package com.krain.mievolauncher.util

import android.animation.ObjectAnimator
import android.transition.TransitionManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.krain.mievolauncher.R
import com.krain.mievolauncher.databinding.ActivityMainBinding

class MainActivityAnimator(private val binding: ActivityMainBinding) {
    private var historyShown = false
    private var main = ConstraintSet()
    private var history = ConstraintSet()
    private var root: ConstraintLayout = binding.root as ConstraintLayout

    init {
        main.clone(root)
        history.clone(root.context, R.layout.activity_main_hist)
    }

    fun toggleHistory(): Boolean {
        TransitionManager.beginDelayedTransition(root)
        val constraint = if (historyShown) main else history
        constraint.applyTo(root)
        historyShown = !historyShown
        rotateChevron(historyShown)
        return historyShown
    }

    private fun rotateChevron(showing: Boolean) {
        val rot = if (showing) -180f else 0f
        ObjectAnimator.ofFloat(binding.chevron, "rotation", rot).apply {
            duration = 100
            start()
        }
    }
}