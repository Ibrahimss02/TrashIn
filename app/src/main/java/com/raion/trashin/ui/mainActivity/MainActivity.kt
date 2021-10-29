package com.raion.trashin.ui.mainActivity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.raion.trashin.databinding.ActivityMainBinding
import com.raion.trashin.ui.ProductResultFragment
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: ExecutorService
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = Executors.newSingleThreadExecutor()

        val mainActivityViewModelFactory = MainActivityViewModelFactory(application)
        viewModel =
            ViewModelProvider(this, mainActivityViewModelFactory)[MainActivityViewModel::class.java]

        binding.lifecycleOwner = this

        if (allPermissionGranted()) {
            startCamera()
        } else {
            requestPermission()
        }

        viewModel.product.observe(this, { product ->
            if (product != null) {
                Log.d(TAG, "observed")
                ProductResultFragment.show(supportFragmentManager, product)
                cameraProvider.unbindAll()
            }
        })

        viewModel.stopCameraPreview.observe(this, {
            if (it) {
                cameraProvider.unbindAll()
            }
        })

        viewModel.onProductAdded.observe(this, {
            Toast.makeText(this, "Product with barcode $it added", Toast.LENGTH_SHORT).show()
            ProductResultFragment.dismiss(supportFragmentManager)
            onBackPressed()
        })
    }

    fun onProductAdded(productId : String) {
        Log.d(TAG, "Freaking function is called with $productId")
        viewModel.addProductToDatabase(productId)
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "All permission must be granted in order to use the app",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        viewModel.onCameraStarted()

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(executor, binding.cameraView.surfaceProvider)
                }

            val analysisUseCase = viewModel.imageAnalysisUseCase

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, analysisUseCase
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera use case binding failed.", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQUEST_CODE = 1
        private const val TAG = "MainActivity"
    }
}