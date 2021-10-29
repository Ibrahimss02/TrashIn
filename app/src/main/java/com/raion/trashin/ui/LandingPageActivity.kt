package com.raion.trashin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        viewModel.onSignOutSuccess.observe(this, {
            if (it) {
                startActivity(Intent(this, SplashScreenActivity::class.java))
                Toast.makeText(this, "Signed Out. Itu tombol buat sign out sementara wkwkw", Toast.LENGTH_SHORT).show()
                viewModel.onNavigatedSignOut()
                finish()
            }
        })
    }
}