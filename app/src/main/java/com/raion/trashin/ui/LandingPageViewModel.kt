package com.raion.trashin.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raion.trashin.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LandingPageViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _currentUser = MutableLiveData<User>()
    val currentUser : LiveData<User>
        get() = _currentUser

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection(USER_PATH).document(auth.currentUser!!.uid).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)

                    user?.let {
                        _currentUser.value = it
                        Log.d(TAG, "Current user ${it.username} with ${it.point} pts")
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message, it)
                }
        }
    }

    companion object {
        private const val USER_PATH = "USER_PATH"
        private const val TAG = "LandingPageViewModel"
        private const val BARCODE_COLLECTION_PATH = "productList"
        private const val BARCODE_ID_FIELD = "barcodeId"
    }
}