package com.voodoolab.eco.ui.tab_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R

// todo https://blog.mindorks.com/implementing-easy-permissions-in-android-android-tutorial easy perms
// todo https://github.com/dm77/barcodescanner

class WashingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scanner_layout, container, false)
        return view
    }
}