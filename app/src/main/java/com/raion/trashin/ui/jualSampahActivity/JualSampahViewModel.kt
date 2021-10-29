package com.raion.trashin.ui.jualSampahActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raion.trashin.dto.Product
import com.raion.trashin.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JualSampahViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _productList = MutableLiveData<ArrayList<Product>>(arrayListOf())
    val productList: LiveData<ArrayList<Product>>
        get() = _productList

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice : LiveData<Int>
        get() = _totalPrice

    private val _totalWeight = MutableLiveData<Int>()
    val totalWeight : LiveData<Int>
        get() = _totalWeight

    private lateinit var currentUser: User

    init {
        getCurrentUser()
    }

    private fun fetchSampahFromDatabase() {
        val allProduct = ArrayList<Product>()
        var totalPrice = 0
        var totalWeight = 0
        viewModelScope.launch(Dispatchers.IO) {
            if (this@JualSampahViewModel::currentUser.isInitialized) {
                if (currentUser.productListId.isNotEmpty()) {
                    db.collection(BARCODE_COLLECTION_PATH)
                        .whereIn(BARCODE_ID_FIELD, currentUser.productListId)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error)
                                return@addSnapshotListener
                            }
                            if (value != null) {
                                val documents = value.documents
                                documents.forEach {
                                    val product = it.toObject(Product::class.java)
                                    if (product != null) {
                                        allProduct.add(product)
                                        totalPrice += product.price.toInt()
                                        totalWeight += product.weight.toInt()
                                    }
                                }
                                _productList.value = allProduct
                                _totalPrice.value = totalPrice
                                _totalWeight.value = totalWeight
                            }

                        }
                }
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection(USER_PATH).document(auth.currentUser!!.uid).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)

                    if (user != null) {
                        currentUser = user
                        fetchSampahFromDatabase()
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message, it)
                }
        }
    }

    companion object {
        private const val USER_PATH = "USER_PATH"
        private const val TAG = "JualSampahViewModel"
        private const val BARCODE_COLLECTION_PATH = "productList"
        private const val BARCODE_ID_FIELD = "barcodeId"
    }

}