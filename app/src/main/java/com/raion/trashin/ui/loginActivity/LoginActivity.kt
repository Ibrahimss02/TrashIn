package com.raion.trashin.ui.loginActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raion.trashin.databinding.ActivityLoginBinding
import com.raion.trashin.ui.LandingPageActivity
import com.raion.trashin.ui.registerActivity.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private val viewModel : LoginActivityViewModel by lazy {
        LoginActivityViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel


        viewModel.onLoginSuccess.observe(this, {
            if (it) {
                startActivity(Intent(this, LandingPageActivity::class.java))
                viewModel.onUserNavigated()
                finish()
            }
        })

        binding.noAccountText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            onPause()
        }
    }
}