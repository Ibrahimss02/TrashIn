package com.raion.trashin.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product (
    val barcodeId : String = "",
    val productName : String = "",
    val weight : String = "",
    val price : String = "",
    val priceInPoint : String = "",
) : Parcelable