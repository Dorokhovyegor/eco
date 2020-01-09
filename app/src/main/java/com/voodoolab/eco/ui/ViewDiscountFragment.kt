package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.WashAdapterRecyclerView
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.discount_state.DiscountStateEvent
import com.voodoolab.eco.ui.view_models.DiscountViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class ViewDiscountFragment : Fragment(), DataStateListener {

    var dataStateHandler: DataStateListener = this
    lateinit var discountViewModel: DiscountViewModel

    private var titleTextView: TextView? = null
    private var bodyTextView: TextView? = null
    private var imageStock: ImageView? = null
    private var progressBar: MaterialProgressBar? = null
    private var washRecyclerView: RecyclerView? = null

    private var adapterRecyclerView: WashAdapterRecyclerView? = null
    private var discountId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        discountId = arguments?.get("id") as Int?
        if (discountId == null) {
            val model = arguments?.getParcelable<SpecialOfferModel>("offer_model")
            discountId = model?.id
        }
        discountViewModel = ViewModelProvider(this).get(DiscountViewModel::class.java)
        return inflater.inflate(R.layout.discount_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleTextView = view.findViewById(R.id.titleTextView)
        bodyTextView = view.findViewById(R.id.bodyTextView)
        imageStock = view.findViewById(R.id.discountImageView)
        progressBar = view.findViewById(R.id.progress_bar)
        washRecyclerView = view.findViewById(R.id.washRecyclerView
        )
        discountId?.let {
            triggerEvent(it)
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
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
            viewState.discountResponse?.let { response ->
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
                    adapterRecyclerView =
                        WashAdapterRecyclerView(response.washes) { model: WashModel ->
                            washClicked(model)
                        }
                    washRecyclerView?.adapter = adapterRecyclerView
                    washRecyclerView?.layoutManager = LinearLayoutManager(context)
                }
            }
        })
    }

    private fun triggerEvent(id: Int) {
        val token = "Bearer ${Hawk.get<String>(Constants.TOKEN)}"
        discountViewModel.setStateEvent(DiscountStateEvent.RequestDiscountById(token, id))
    }

    private fun washClicked(washModel: WashModel) {
        view?.let {
            val list = adapterRecyclerView?.items
            val bundle = bundleOf(
                "wash_list" to list,
                "current_wash" to washModel
            )
            Navigation.findNavController(it).navigate(R.id.action_viewDiscountFragment_to_washOnMapFragment, bundle)
        }
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            progressBar?.show(dataState.loading)
        }
    }
}

