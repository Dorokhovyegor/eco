package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.discount_state.DiscountStateEvent
import com.voodoolab.eco.ui.view_models.DiscountViewModel
import com.voodoolab.eco.utils.Constants

class ViewDiscountFragment(discountId: Int) : Fragment(), DataStateListener {

    var dataStateHandler: DataStateListener = this

    lateinit var discountViewModel: DiscountViewModel

    private var titleTextView: TextView? = null
    private var bodyTextView: TextView? = null
    private var imageStock: ImageView? = null
    private var washRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        discountViewModel = ViewModelProvider(this).get(DiscountViewModel::class.java)
        return inflater.inflate(R.layout.discount_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleTextView = view.findViewById(R.id.titleTextView)
        bodyTextView = view.findViewById(R.id.discountImageView)
        imageStock = view.findViewById(R.id.discountImageView)
        washRecyclerView?.run {
            this.layoutManager = LinearLayoutManager(context)
        }
        triggerEvent(id)
        subscribeObservers()
    }

    fun subscribeObservers() {
        discountViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { viewState ->
                viewState.getContentIfNotHandled()?.let {
                    it.discountResponse?.let {
                        discountViewModel.setDiscountResponse(it)
                    }
                }
            }
        })

        discountViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.discountResponse?.let {response ->
                if (response.status == "ok") {
                    titleTextView?.text = response.title
                    bodyTextView?.text = response.text
                    imageStock?.let {
                        if (context != null) {
                            Glide.with(context!!)
                                .load(response.logo)
                                .placeholder(R.drawable.empty_discount)
                                .error(R.drawable.empty_discount)
                                .into(it)
                        }

                        // todo create adapter and set it
                    }
                }
            }
        })
    }

    private fun triggerEvent(id: Int) {
        val token = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
        discountViewModel.setStateEvent(DiscountStateEvent.RequestDiscountById(token, id))
    }

    override fun onDataStateChange(dataState: DataState<*>?) {

    }
}