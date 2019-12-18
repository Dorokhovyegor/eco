package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.voodoolab.eco.R

class ObjectInfoBottomSheet(var arg: Bundle?): BottomSheetDialogFragment() {

    private var addressTextView: TextView? = null
    private var cashBack: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.object_info_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addressTextView = view.findViewById(R.id.address)
        cashBack = view.findViewById(R.id.cashBack)

        val address = arg?.getString("address")
        val cashbackValue = arg?.getInt("cashback")

        addressTextView?.text = address
        cashBack?.text = getString(R.string.value_of_cashback, cashbackValue)
    }
}