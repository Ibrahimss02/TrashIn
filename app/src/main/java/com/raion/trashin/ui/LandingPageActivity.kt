package com.raion.trashin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raion.trashin.databinding.ActivityLandingPageBinding
import com.raion.trashin.ui.jualSampahActivity.JualSampahActivity
import com.raion.trashin.ui.mainActivity.MainActivity

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLandingPageBinding

    private val viewModel : LandingPageViewModel by lazy {
        LandingPageViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.background = null
        binding.bottomNavView.itemIconTintList = null
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.fabScan.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            onPause()
        }

        binding.jualSampahContainer.setOnClickListener {
            startActivity(Intent(this, JualSampahActivity::class.java))
            onPause()
        }
    }
}