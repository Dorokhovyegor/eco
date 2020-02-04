package com.voodoolab.eco.helper_fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.SendReportInterface
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.utils.Constants


class SendReportBottomSheet(
    val data: Bundle?
) : BottomSheetDialogFragment() {

    var button: Button? = null
    var inputReport: TextInputEditText? = null
    var ratingBar: RatingBar? = null
    var sendReportInterface: SendReportInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val bottomSheet =
                    (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = 360
                }
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        )
        setContentAndListeners(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            sendReportInterface = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        sendReportInterface = null
    }

    private fun setContentAndListeners(view: View) {
        data?.let {
            val value = it.getInt(Constants.NOTIFICATION_VALUE_OF_TRANSACTION)
            view.findViewById<TextView>(R.id.valueTextView).text =
                Html.fromHtml(
                    context?.resources?.getString(
                        R.string.balance_value,
                        value.div(100).toString()
                    ), 0
                )
            val transactionId = it.getInt(Constants.NOTIFICATION_OPERATION_ID)
            val address = it.getString(Constants.NOTIFICATION_WASH_ADDRESS)
            val city = it.getString(Constants.NOTIFICATION_WASH_CITY)

            view.findViewById<TextView>(R.id.dateTextView).text //todo data
            view.findViewById<TextView>(R.id.addressTextView).text =
                getString(R.string.full_address, city, address)

            button = view.findViewById(R.id.report_button)
            inputReport = view.findViewById(R.id.report_edit_text)
            ratingBar = view.findViewById(R.id.ratingBar)

            button?.setOnClickListener {
                sendReportInterface?.sendReportClick(
                    id = transactionId,
                    text = inputReport?.text.toString(),
                    ratio = ratingBar?.rating!!.toDouble()
                )
                dismiss()
            }
        }
    }
}