package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.SpecialOfferBottomSheetRecyclerViewAdapter
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.responses.ObjectResponse

class ObjectInfoBottomSheet(
    var arg: Bundle?,
    val block: (model: SpecialOfferModel) -> Unit
) : BottomSheetDialogFragment() {

    private var addressTextView: TextView? = null
    private var cashBack: TextView? = null
    private var happyHours: TextView? = null

    private var titleSpecialOffers: TextView? = null
    private var specialOfferRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.object_info_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val objectInfo = arg?.getParcelable<ObjectResponse>("object_info")
        val items = objectInfo?.stocks

        addressTextView = view.findViewById(R.id.addressTextView)

        val includeLayout = view.findViewById<ConstraintLayout>(R.id.details_info)
        cashBack = includeLayout.findViewById(R.id.cashbackTextView)
        happyHours = includeLayout.findViewById(R.id.timeTextView)

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

        addressTextView?.text = objectInfo?.address
        objectInfo?.happyHoursInfo?.active?.let {
            if (it) {
                happyHours?.text = Html.fromHtml(getString(
                    R.string.happy_hours_enable_time,
                    objectInfo.happyHoursInfo.start,
                    objectInfo.happyHoursInfo.end
                ), 0)
            } else {
                happyHours?.text = Html.fromHtml(getString(
                    R.string.happy_hours_disable_time,
                    objectInfo.happyHoursInfo.start,
                    objectInfo.happyHoursInfo.end
                ), 0)
            }
        }

        cashBack?.text = Html.fromHtml(getString(R.string.cashback_info, objectInfo?.cashback), 0)
    }

    private fun clickOffer(data: SpecialOfferModel) {
        block(data)
    }
}