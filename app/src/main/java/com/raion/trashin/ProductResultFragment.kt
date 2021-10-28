package com.raion.trashin

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raion.trashin.dto.Product

class ProductResultFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.result_bottom_sheet, container)

        val arguments = arguments
        val product =
            if (arguments?.containsKey(ARG_PRODUCT) == true) {
                arguments.getParcelable(ARG_PRODUCT) ?: Product()
            } else {
                Log.e(TAG, "No product passed in!")
                Product()
            }

//        TODO(Implement Product Image)
//        view.findViewById<ImageView>(R.id.image)

        view.findViewById<TextView>(R.id.productName).apply {
            text = product.productName
        }

        view.findViewById<TextView>(R.id.productId).apply {
            text = product.barcodeId
        }

        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        // Tell camerax to start again (unfreeze)
        activity?.let {
            (activity as MainActivity).startCamera()
        }

        super.onDismiss(dialog)
    }

    companion object {

        private const val TAG = "ProductResultFragment"
        private const val ARG_PRODUCT = "arg_product"

        fun show(fragmentManager: FragmentManager, product : Product) {
            val productResultFragment = ProductResultFragment()

            productResultFragment.arguments = Bundle().apply {
                putParcelable(ARG_PRODUCT, product)
            }

            productResultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as ProductResultFragment?)?.dismiss()
        }

    }
}