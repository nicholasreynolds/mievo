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
import androidx.recyclerview.widget.LinearLayoutManager
import com.krain.mievolauncher.command.CommandFactory

import com.krain.mievolauncher.databinding.ActivityMainBinding
import com.krain.mievolauncher.room.model.Command
import com.krain.mievolauncher.util.MainActivityAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), View.OnTouchListener, CoroutineScope by MainScope(),
    View.OnScrollChangeListener {
    val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var anim: MainActivityAnimator
    private lateinit var imm: InputMethodManager
    private var scrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        viewModel.appContext = applicationContext
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        anim = MainActivityAnimator(binding)
        binding.apply {
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
                if(viewModel.processQuery(query.text)) {
                    clearInput()
                }
                return@setOnEditorActionListener true
            } // disable enter key
            suggestions.setOnScrollChangeListener(this@MainActivity)
            suggestions.setOnTouchListener(this@MainActivity)
            history.setOnScrollChangeListener(this@MainActivity)
            history.setOnTouchListener(this@MainActivity)
            commands.setOnScrollChangeListener(this@MainActivity)
            commands.setOnTouchListener(this@MainActivity)
            chevron.setOnClickListener {
                toggleHistory()
                viewModel.updateSuggestions(query.text)
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
        v.performClick()
        if (me.action == MotionEvent.ACTION_UP) {
            if (!scrolling) {
                focusInput()
                return true
            }
            scrolling = false
        }
        return false
    }

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        scrolling = true
    }

    fun launchApp(intent: Intent?) {
        if (intent == null) {
            return
        }
        viewModel.incrementUsage(intent.`package`)
        viewModel.insertHistory(binding.query.text.toString())
        clearInput()
        startActivity(intent)
    }

    fun setCommand(cmd: Command) {
        setQuery(cmd.name)
        viewModel.command = CommandFactory.getInstance(cmd.type)
    }

    fun setQuery(query: String?) {
        if (query == null) {
            return
        }
        toggleHistory()
        binding.query.setText(query)
    }

    private fun toggleHistory() {
        anim.toggleHistory()
        viewModel.switchMode()
    }

    // Focus command prompt and show keyboard
    private fun focusInput() {
        binding.query.requestFocus()
        imm.showSoftInput(currentFocus, 0)
    }

    // Clear command prompt and hide keyboard
    private fun clearInput() {
        binding.query.setText("")
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}