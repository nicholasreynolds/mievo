package com.krain.mievolauncher.util

import android.transition.TransitionManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.krain.mievolauncher.R

class MainActivityAnimator(private val root: ConstraintLayout) {
    private var historyShown = false
    private var main = ConstraintSet()
    private var history = ConstraintSet()

    init {
        main.clone(root)
        history.clone(root.context, R.layout.activity_main_hist)
    }

    fun toggleHistory() {
        TransitionManager.beginDelayedTransition(root)
        val constraint = if (historyShown) main else history
        constraint.applyTo(root)
        historyShown = !historyShown
    }
}