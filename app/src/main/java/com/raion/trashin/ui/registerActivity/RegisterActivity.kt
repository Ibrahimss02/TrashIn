package com.raion.trashin.ui.registerActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raion.trashin.databinding.ActivityRegisterBinding
import com.raion.trashin.ui.LandingPageActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding

    private val viewModel : RegisterActivityViewModel by lazy {
        RegisterActivityViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel

        viewModel.registerSuccess.observe(this, {
            if (it) {
                onBackPressed()
                viewModel.onSuccessNavigated()
                finish()
            }
        })
    }
}