package com.krain.mievolauncher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.krain.mievolauncher.databinding.ActivityMainBinding
import com.krain.mievolauncher.recyclerview.adapter.HistoryAdapter
import com.krain.mievolauncher.recyclerview.adapter.SuggestionsAdapter
import com.krain.mievolauncher.util.MainActivityAnimator

class MainActivity : AppCompatActivity(), View.OnTouchListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var anim: MainActivityAnimator
    private lateinit var imm : InputMethodManager
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        viewModel.appContext = applicationContext
        viewModel.suggestionsAdapter = SuggestionsAdapter()
        viewModel.historyAdapter = HistoryAdapter()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        anim = MainActivityAnimator(binding)
        binding.apply {
            suggestions.adapter = viewModel.suggestionsAdapter
            history.adapter = viewModel.historyAdapter
            command.addTextChangedListener(
                { _, _, _, _ -> },
                { charSequence: CharSequence?, _, _, _ -> viewModel.updateSuggestions(charSequence)},
                {}
            )
            suggestions.setOnTouchListener(this@MainActivity)
            history.setOnTouchListener(this@MainActivity)
            chevron.setOnClickListener{
                toggleHistory()
                viewModel.updateSuggestions(command.text)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
        focusInput()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.appContext = null
    }

    override fun onTouch(v: View, me: MotionEvent) : Boolean {
        if (me.action == MotionEvent.ACTION_UP) {
            v.performClick()
            focusInput()
            return true
        }
        return false
    }

    fun launchApp(intent: Intent?) {
        if (intent == null) {
            return
        }
        viewModel.insertHistory(binding.command.text.toString())
        clearInput()
        startActivity(intent)
    }

    fun setCommand(cmd: String?) {
        if (cmd == null) {
            return
        }
        toggleHistory()
        binding.command.setText(cmd)
    }

    private fun toggleHistory() {
        viewModel.showHistory = anim.toggleHistory()
    }

    // Focus command prompt and show keyboard
    private fun focusInput() {
        binding.command.requestFocus()
        imm.showSoftInput(currentFocus, 0)
    }

    // Clear command prompt and hide keyboard
    private fun clearInput() {
        binding.command.setText("")
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}