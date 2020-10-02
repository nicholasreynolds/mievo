package com.krain.mievolauncher

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.krain.mievolauncher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        viewModel.pm = this.packageManager
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.command.addTextChangedListener(
            { _,_,_,_ -> },
            { charSequence: CharSequence?,_,_,_ ->
                viewModel.updateSuggestions(charSequence)
            },
            {}
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }
}