package com.raion.trashin.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.raion.trashin.R
import com.raion.trashin.dto.Product
import com.raion.trashin.ui.mainActivity.MainActivity

class ProductResultFragment : BottomSheetDialogFragment() {

    private val _onClickAdd = MutableLiveData<String>()
    val onClickAdd : LiveData<String>
        get() = _onClickAdd

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

        view.findViewById<MaterialButton>(R.id.addProductButton).apply {
            setOnClickListener {
                activity?.let {
                    (it as MainActivity).onProductAdded(product.barcodeId)
                }
            }
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
            Log.d(TAG, "Fragment Showed")

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