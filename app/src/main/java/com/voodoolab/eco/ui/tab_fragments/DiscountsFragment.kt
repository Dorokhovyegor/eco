package com.voodoolab.eco.ui.tab_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.utils.Constants

class DiscountsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.discounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbarContent(view)
    }

    private fun setToolbarContent(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.let {
            val pref = it.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
            val city = pref.getString(Constants.CITY_ECO, null)
            city?.let {
                toolbar.subtitle = it
            }
        }
    }
}