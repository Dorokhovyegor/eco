package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.SpecialOfferBottomSheetRecyclerViewAdapter
import com.voodoolab.eco.models.SpecialOfferModel

class ObjectInfoBottomSheet(var arg: Bundle?,
                            val block: (model: SpecialOfferModel) -> Unit): BottomSheetDialogFragment() {

    private var addressTextView: TextView? = null
    private var cashBack: TextView? = null

    private var titleSpecialOffers: TextView? = null
    private var specialOfferRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.object_info_layout, container,false)
    }

    override fun onViewCreated(view : View, savedInstanceState: Bundle?) {
        val address = arg?.getString("address")
        val cashbackValue = arg?.getInt("cashback")
        val items = arg?.getParcelableArrayList<SpecialOfferModel>("special_offers")

        addressTextView = view.findViewById(R.id.address)
        cashBack = view.findViewById(R.id.cashBack)

        titleSpecialOffers = view.findViewById(R.id.titleSpecialOffer)
        specialOfferRecyclerView = view.findViewById(R.id.specialOfferRecyclerView)

        items?.let {
            if (it.size != 0) {
                specialOfferRecyclerView?.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                specialOfferRecyclerView?.adapter =
                    SpecialOfferBottomSheetRecyclerViewAdapter(items) { data ->
                        clickOffer(data)
                    }
            } else {
                specialOfferRecyclerView?.visibility = View.GONE
                titleSpecialOffers?.visibility = View.GONE
            }
        }


        addressTextView?.text = address
        cashBack?.text = getString(R.string.value_of_cashback, cashbackValue)
    }

    private fun clickOffer(data: SpecialOfferModel) {
        block(data)
    }
}