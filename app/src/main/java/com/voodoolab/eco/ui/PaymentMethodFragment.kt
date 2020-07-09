package com.voodoolab.eco.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.payment_state.PaymentStateEvent
import com.voodoolab.eco.ui.view_models.PaymentViewModel
import com.voodoolab.eco.utils.Constants
import kotlinx.android.synthetic.main.payment_method_layout.*


class PaymentMethodFragment : Fragment(), DataStateListener {

    lateinit var paymentViewModel: PaymentViewModel
    private var toolbar: Toolbar? = null
    var dataStateHandler: DataStateListener = this


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_method_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        toolbar = view.findViewById(R.id.toolbar)
        web_button.setOnClickListener {
            activity?.let {
                if (it is MainActivity) {
                    it.findViewById<Toolbar>(R.id.toolbar)?.title = "Оплата"
                }
                if (!money.text.isNullOrBlank()) {
                    // todo сделать запрос здесь
                    paymentViewModel.setStateEvent(
                        PaymentStateEvent.RequestPaymentWebUrl(
                            Hawk.get<String>(
                                Constants.TOKEN
                            ), money.text.toString().toInt()
                        )
                    )
                } else {
                    money.error = "Заполните поле"
                }
            }
        }

        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        subscribeObserver()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.loading?.let {
            if (it) {
                progress_bar.visibility = View.VISIBLE
            } else {
                progress_bar.visibility = View.GONE
            }
        }
    }

    private fun subscribeObserver() {
        paymentViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { event ->
                event.getContentIfNotHandled()?.let { viewState ->
                    viewState.paymentHtml?.let {
                        paymentViewModel.setWebHtmlResponse(it)
                    }
                }
            }
        })

        paymentViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState.paymentHtml != null) {
                // todo здесь переходим на соседний fragment
                findNavController().navigate(R.id.action_paymentMethodFragment_to_webFragment, bundleOf("html" to viewState.paymentHtml))
            }
        })
    }
}