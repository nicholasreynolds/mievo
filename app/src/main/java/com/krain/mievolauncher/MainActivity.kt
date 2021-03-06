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
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krain.mievolauncher.command.CommandFactory

import com.krain.mievolauncher.databinding.ActivityMainBinding
import com.krain.mievolauncher.room.model.Command
import com.krain.mievolauncher.util.MainActivityAnimator
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), View.OnTouchListener, CoroutineScope by MainScope() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var anim: MainActivityAnimator
    private lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        viewModel.appContext = applicationContext
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        anim = MainActivityAnimator(binding)
        with(binding) {
            suggestions.adapter = viewModel.suggestionsAdapter
            history.adapter = viewModel.historyAdapter
            history.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
            commands.adapter = viewModel.commandsAdapter
            commands.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            query.addTextChangedListener(
                { _, _, _, _ -> },
                { charSequence: CharSequence?, _, _, _ -> viewModel.updateSuggestions(charSequence) },
                {}
            )
            query.setOnEditorActionListener { _, _, _ ->
                viewModel.viewModelScope.launch {
                    if (viewModel.processQuery(query.text)) {
                        insertQueryToHistory()
                        clearText()
                    }
                }
                return@setOnEditorActionListener true
            } // disable enter key
            arrayOf(
                suggestions,
                history,
                commands
            ).forEach {
                it.setOnTouchListener(this@MainActivity)
            }
            chevron.setOnClickListener {
                toggleHistory()
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

    override fun onTouch(v: View, me: MotionEvent): Boolean {
        if (me.action == MotionEvent.ACTION_UP) {
            if ((v as RecyclerView).scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                v.performClick()
                focusInput()
                return true
            }
        }
        return false
    }

    fun launchApp(name: String, intent: Intent?) {
        if (intent == null) {
            return
        }
        viewModel.incrementUsage(intent.`package`)
        setQuery(name)
        insertQueryToHistory()
        clearInput()
        startActivity(intent)
    }

    fun setQuery(query: String?) {
        if (query == null) {
            return
        }
        binding.query.setText(query)
        binding.query.setSelection(query.length)
    }

    fun toggleHistory() {
        anim.toggleHistory()
        viewModel.switchMode()
        viewModel.updateSuggestions(binding.query.text)
    }

    // Focus command prompt and show keyboard
    private fun focusInput() {
        binding.query.requestFocus()
        imm.showSoftInput(currentFocus, 0)
    }

    private fun insertQueryToHistory() {
        viewModel.insertHistory(binding.query.text.toString())
    }

    // Clear command prompt and hide keyboard
    private fun clearInput() {
        clearText()
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun clearText() {
        binding.query.setText("")
    }
}