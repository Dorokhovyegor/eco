package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.voodoolab.eco.R


class PaymentMethodFragment : Fragment() {

    private var price: TextInputEditText? = null
    private var priceLayout: TextInputLayout? = null

    private var toolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_method_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar = view.findViewById(R.id.toolbar)
        price = view.findViewById(R.id.money)
        priceLayout = view.findViewById(R.id.phone_input_layout)
        val goToWebViewButton = view.findViewById<Button>(R.id.web_button)
        goToWebViewButton?.setOnClickListener {
            activity?.let {
                if (it is MainActivity) {
                    it.findViewById<Toolbar>(R.id.toolbar)?.title = "Оплата"
                }
                if (!price?.text.isNullOrBlank()) {
                    it.findNavController(R.id.frame_container).navigate(R.id.webFragment)
                } else {
                    price?.error = "Заполните поле"
                }
            }
        }

        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

    }
}