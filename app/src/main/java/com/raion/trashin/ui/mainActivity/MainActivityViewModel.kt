package com.raion.trashin.ui.mainActivity

import android.app.Application
import android.util.Log
import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.raion.trashin.dto.Product
import com.raion.trashin.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

typealias BarcodeValueListener = (value: String) -> Unit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var executor = Executors.newSingleThreadExecutor()
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?>
        get() = _product

    private val _stopCameraPreview = MutableLiveData(false)
    val stopCameraPreview: LiveData<Boolean>
        get() = _stopCameraPreview

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val collectionRef = db.collection(BARCODE_COLLECTION_PATH)

    private val _onProductAdded = MutableLiveData<String>()
    val onProductAdded : LiveData<String>
        get() = _onProductAdded

    private val _noData = MutableLiveData<String>()
    val noData : LiveData<String>
        get() = _noData

    private lateinit var currentUser: User

    init {
        viewModelScope.launch(Dispatchers.IO) {
            auth.currentUser?.let { user ->
                db.collection(USER_PATH).document(user.uid).get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject(User::class.java)

                        if (user != null) {
                            currentUser = user
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, it.message, it)
                    }
            }
        }
    }

    @ExperimentalGetImage
    var imageAnalysisUseCase = ImageAnalysis.Builder()
        .setTargetResolution(Size(140, 100))
        .build()
        .also {
            it.setAnalyzer(
                executor, BarcodeAnalyzer { value ->
                    Log.d(TAG, "Barcode value : $value")
                    _stopCameraPreview.value = false

                    collectionRef.whereEqualTo(BARCODE_ID_FIELD, value)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty){
                                for (document in documents) {
                                    Log.d(TAG, "${document.id} -> ${document.data}")
                                    val product = document.toObject(Product::class.java)

                                    if (product.productName.isNotEmpty()) {
                                        _product.value = product
                                    }
                                }
                            } else {
                                _noData.value = value
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error getting documents: ", e)
                        }

                }
            )
        }

    fun addProductToDatabase(productId: String) {
        Log.d(TAG, "Adding product $productId to database")
        viewModelScope.launch(Dispatchers.IO) {
            if (this@MainActivityViewModel::currentUser.isInitialized) {
                currentUser.productListId.add(productId)
                db.collection(USER_PATH).document(currentUser.id)
                    .set(currentUser, SetOptions.merge())
                    .addOnSuccessListener {
                        _onProductAdded.value = productId
                    }
            }
        }
    }

    @ExperimentalGetImage
    internal class BarcodeAnalyzer(private val listener: BarcodeValueListener) :
        ImageAnalysis.Analyzer {

        private val barcodeOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
        ).build()

        override fun analyze(image: ImageProxy) {
            val mediaImage = image.image
            if (mediaImage != null) {
                val imageInput =
                    InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

                val scanner = BarcodeScanning.getClient(barcodeOptions)

                scanner.process(imageInput)
                    .addOnSuccessListener { barcodeList ->
                        val barcode = barcodeList.getOrNull(0)

                        barcode?.rawValue?.let {
                            listener(it)
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, it.message.orEmpty())
                    }
                    .addOnCompleteListener {
                        image.image?.close()
                        image.close()
                    }
            }
        }
    }

    fun onCameraStarted() {
        _stopCameraPreview.value = false
    }

    companion object {
        private const val USER_PATH = "USER_PATH"
        private const val TAG = "ActivityMainViewModel"
        private const val BARCODE_COLLECTION_PATH = "productList"
        private const val BARCODE_ID_FIELD = "barcodeId"
    }

}