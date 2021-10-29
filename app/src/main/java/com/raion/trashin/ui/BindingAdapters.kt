package com.raion.trashin.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raion.trashin.dto.Product
import com.raion.trashin.ui.jualSampahActivity.ProductAdapter

@BindingAdapter("listDataProduct")
fun bindProductItems(recyclerView: RecyclerView, products : ArrayList<Product>) {
    val adapter = recyclerView.adapter as ProductAdapter
    adapter.submitList(products)
}

@BindingAdapter("totalPriceFormat")
fun formatTotalPrice(textView: TextView, totalPrice : Int) {
    textView.text = String.format("Rp %,d", totalPrice)
}

@BindingAdapter("totalWeightFormat")
fun formatTotalWeight(textView: TextView, totalWeight : Int) {
    textView.text = String.format("%d g", totalWeight)
}

@BindingAdapter("totalPointFormat")
fun formatTotalPoint(textView: TextView, totalPoint : Int) {
    textView.text = String.format("%,d", totalPoint)
}