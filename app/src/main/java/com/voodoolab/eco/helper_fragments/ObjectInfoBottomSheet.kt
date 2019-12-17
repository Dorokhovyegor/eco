package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.voodoolab.eco.R

class ObjectInfoBottomSheet: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.object_info_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}