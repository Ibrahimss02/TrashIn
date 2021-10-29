package com.raion.trashin.ui.jualSampahActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raion.trashin.databinding.ActivityJualSampahBinding

class JualSampahActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJualSampahBinding

    private val viewModel : JualSampahViewModel by lazy {
        JualSampahViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJualSampahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.sampahRv.adapter = ProductAdapter()

        binding.upButton.setOnClickListener {
            onBackPressed()
        }
    }
}