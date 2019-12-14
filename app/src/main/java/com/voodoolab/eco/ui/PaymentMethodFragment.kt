package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.voodoolab.eco.R

class PaymentMethodFragment : Fragment() {

    private var price: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_method_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        price = view.findViewById(R.id.price_input)
        val goToWebViewButton = view.findViewById<Button>(R.id.web_button)
        goToWebViewButton?.setOnClickListener {
            activity?.let {
                if (it is MainActivity) {
                    it.supportActionBar?.title = "Оплата"
                }
                if (!price?.text.isNullOrBlank()) {
                    it.findNavController(R.id.frame_container).navigate(R.id.webFragment)
                } else {
                    price?.error = "Заполните поле"
                }

            }
        }

    }



}