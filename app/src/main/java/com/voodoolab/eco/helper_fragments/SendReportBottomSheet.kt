package com.voodoolab.eco.helper_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.voodoolab.eco.R

class SendReportBottomSheet(var arg: Bundle?) : BottomSheetDialogFragment() {

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
                    (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                BottomSheetBehavior.from<View>(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = 360
                }
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}