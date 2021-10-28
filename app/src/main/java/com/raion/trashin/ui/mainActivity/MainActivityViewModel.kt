package com.raion.trashin.ui.mainActivity

import android.app.Application
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.raion.trashin.dto.Product
import java.util.concurrent.Executors

typealias BarcodeValueListener = (value : String) -> Unit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var executor = Executors.newSingleThreadExecutor()
    private val _product = MutableLiveData<Product?>()
    val product : LiveData<Product?>
        get() = _product

    private val db = Firebase.firestore
    private val collectionRef = db.collection(BARCODE_COLLECTION_PATH)

    @ExperimentalGetImage
    var imageAnalysisUseCase = ImageAnalysis.Builder()
        .build()
        .also {
            it.setAnalyzer(
                executor, BarcodeAnalyzer { value ->
                    Log.d(TAG, "Barcode value : $value")

                    collectionRef.whereEqualTo(BARCODE_ID_FIELD, value)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d(TAG, "${document.id} -> ${document.data}")
                                val product = document.toObject(Product::class.java)

                                if (product.productName.isNotEmpty()) {
                                    _product.value = product
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error getting documents: ", e)
                        }

                }
            )
        }

    @ExperimentalGetImage
    internal class BarcodeAnalyzer(private val listener : BarcodeValueListener) : ImageAnalysis.Analyzer {

        private val barcodeOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
        ).build()

        override fun analyze(image: ImageProxy) {
            val mediaImage = image.image
            if(mediaImage != null) {
                val imageInput = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

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

    companion object {
        private const val TAG = "ActivityMainViewModel"
        private const val BARCODE_COLLECTION_PATH = "productList"
        private const val BARCODE_ID_FIELD = "barcodeId"
    }

}