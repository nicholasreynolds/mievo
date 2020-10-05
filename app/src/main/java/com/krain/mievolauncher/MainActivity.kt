package com.krain.mievolauncher

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.krain.mievolauncher.databinding.ActivityMainBinding
import com.krain.mievolauncher.recyclerview.SuggestionsAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }
}