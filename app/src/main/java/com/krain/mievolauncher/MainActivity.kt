package com.krain.mievolauncher

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.krain.mievolauncher.databinding.ActivityMainBinding
import com.krain.mievolauncher.recyclerview.SuggestionsAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imm : InputMethodManager
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        viewModel.appContext = applicationContext
        viewModel.suggestionsAdapter = SuggestionsAdapter()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.suggestions.adapter = viewModel.suggestionsAdapter
        binding.command.addTextChangedListener(
            { _, _, _, _ -> },
            { charSequence: CharSequence?, _, _, _ -> viewModel.updateSuggestions(charSequence) },
            {}
        )
        binding.suggestions.setOnTouchListener { v,_ ->
            v.performClick()
            onTouch()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }

    private fun onTouch() : Boolean {
        if (viewModel.suggestionsAdapter.suggestions.length() == 0) {
            binding.command.requestFocus()
            showKeyboard()
            return true
        }
        return false
    }

    private fun showKeyboard() {
        imm.showSoftInput(currentFocus, 0)
    }
}