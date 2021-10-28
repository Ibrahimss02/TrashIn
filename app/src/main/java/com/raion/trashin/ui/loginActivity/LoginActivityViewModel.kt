package com.raion.trashin.ui.loginActivity

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivityViewModel : ViewModel() {

    private val auth = Firebase.auth

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    fun onClickLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            val email = email.value!!.trim { it <= ' ' }
            val password = password.value!!

            if (validateForm(email, password)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Auth", "Login Success")
                            // TODO signin successful navigate to MainActivity
                        } else {
                            // TODO show error dialog to user
                            Log.d("Auth", "Login unsuccessful")
                        }
                    }
            }
        }
    }

    private fun validateForm(email : String, password : String) : Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                // Show error to User
                false
            }
            TextUtils.isEmpty(password) -> {
                // Show error to User
                false
            }
            else -> true
        }
    }
}