package com.raion.trashin.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product (
    val barcodeId : String = "8992222054492",
    val productName : String = "Product Name"
) : Parcelable