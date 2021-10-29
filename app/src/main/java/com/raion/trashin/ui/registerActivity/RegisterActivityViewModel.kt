package com.raion.trashin.ui.registerActivity

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raion.trashin.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivityViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordConfirm = MutableLiveData<String>()
    val username = MutableLiveData<String>()

    private val _registerSuccess = MutableLiveData(false)
    val registerSuccess : LiveData<Boolean>
        get() = _registerSuccess

    fun onClickRegister() {
        val email = email.value!!.trim { it <= ' ' }
        val password = password.value!!
        val passwordConfirm = passwordConfirm.value!!
        val username = username.value!!

        if (validateForm(email, password, passwordConfirm, username)) {
            viewModelScope.launch(Dispatchers.IO){
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = task.result.user!!
                            val user = User(firebaseUser.uid, email, username)

                            uploadUserToDatabase(user)
                        } else {
                            // TODO Notify User signUp failed
                            Log.d(TAG, "Sign Up Unsuccessful")
                        }
                    }
            }
        }
    }

    private fun validateForm(email : String, password : String, passwordConfirm : String, username : String) : Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                // Show error to User
                false
            }
            TextUtils.isEmpty(password) -> {
                // Show error to User
                false
            }
            TextUtils.isEmpty(username) -> {
                // Show error to User
                false
            }
            !TextUtils.equals(password, passwordConfirm) -> {
                // Show error to User
                false
            }
            else -> true
        }
    }

    private fun uploadUserToDatabase(user : User) {
        viewModelScope.launch (Dispatchers.IO){
            db.collection(USER_PATH).document(user.id).set(user, SetOptions.merge())
                .addOnSuccessListener {
                    // TODO Upload success navigate to Main Activity
                    Log.d(TAG, "${user.username} user upload success")
                    _registerSuccess.value = true
                }
                .addOnFailureListener {
                    // TODO Upload unsuccessful
                    Log.e(TAG, it.message, it)
                }
        }
    }

    fun onSuccessNavigated() {
        _registerSuccess.value = false
    }

    companion object {
        private const val USER_PATH = "USER_PATH"
        private const val TAG = "RegisterViewModel"
    }
}