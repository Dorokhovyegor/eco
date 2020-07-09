package com.voodoolab.eco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
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
import com.voodoolab.eco.states.discount_state.SpecialOfferStateEvent
import com.voodoolab.eco.ui.specialoffers.viewmodel.SpecialOfferViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class ViewDiscountFragment : Fragment(), DataStateListener {

    var dataStateHandler: DataStateListener = this
    lateinit var specialOfferViewModel: SpecialOfferViewModel

    private var titleTextView: TextView? = null
    private var bodyTextView: TextView? = null
    private var imageStock: ImageView? = null
    private var progressBar: MaterialProgressBar? = null
    private var washRecyclerView: RecyclerView? = null
    private var cardView: CardView? = null
    private var toolbar: Toolbar? = null

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
        specialOfferViewModel = ViewModelProvider(this).get(SpecialOfferViewModel::class.java)
        return inflater.inflate(R.layout.discount_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleTextView = view.findViewById(R.id.titleTextView)
        bodyTextView = view.findViewById(R.id.bodyTextView)
        imageStock = view.findViewById(R.id.discountImageView)
        progressBar = view.findViewById(R.id.progress_bar)
        cardView = view.findViewById(R.id.cardView)
        toolbar = view.findViewById(R.id.toolbar)

        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        washRecyclerView = view.findViewById(
            R.id.washRecyclerView
        )
        discountId?.let {
            triggerEvent(it)
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        specialOfferViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { viewState ->
                viewState.getContentIfNotHandled()?.let {
                    it.discountResponse?.let {
                        specialOfferViewModel.setDiscountResponse(it)
                    }
                }
            }
        })

        specialOfferViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.discountResponse?.let { response ->
                titleTextView?.text = response.title
                bodyTextView?.text = response.text

                imageStock?.let {
                    if (context != null) {
                        if (response.logo != "") {
                            Glide.with(context!!)
                                .load(response.logo)
                                .into(it)
                        } else {
                            it.visibility = View.GONE
                        }
                    }

                        // fixme это действие долгое
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
        specialOfferViewModel.setStateEvent(SpecialOfferStateEvent.RequestSpecialOfferById(token, id))
    }

    private fun washClicked(washModel: WashModel) {
        view?.let {
            val list = adapterRecyclerView?.items
            val bundle = bundleOf(
                "wash_list" to list,
                "current_wash" to washModel
            )
            Navigation.findNavController(it)
                .navigate(R.id.action_viewDiscountFragment_to_washOnMapFragment, bundle)
        }
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            progressBar?.show(dataState.loading)
        }
    }
}

