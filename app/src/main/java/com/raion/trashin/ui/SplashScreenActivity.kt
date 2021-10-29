package com.raion.trashin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raion.trashin.R
import com.raion.trashin.ui.loginActivity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            if (auth.currentUser != null) {
                startActivity(Intent(this@SplashScreenActivity, LandingPageActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                finish()
            }
        }
    }
}