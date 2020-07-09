package com.voodoolab.eco.helper_activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.WashAdapterRecyclerView
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.api.specialofferdto.SpecialOfferDto
import com.voodoolab.eco.states.discount_state.SpecialOfferStateEvent
import com.voodoolab.eco.states.view_discounts_state.SetViewedDiscountStateEvent
import com.voodoolab.eco.ui.specialoffers.viewmodel.SpecialOfferViewModel
import com.voodoolab.eco.ui.view_models.ViewDiscountViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class ViewSpecialOfferActivity : AppCompatActivity(), DataStateListener {

    lateinit var specialOfferViewModel: SpecialOfferViewModel
    lateinit var viewDiscountViewModel: ViewDiscountViewModel

    var progressBar: MaterialProgressBar? = null
    var titleSpecialOffer: TextView? = null
    var bodyTextSpecialOffer: TextView? = null
    var specialOfferImageView: ImageView? = null

    private var adapterRecyclerView: WashAdapterRecyclerView? = null
    var listWashes: RecyclerView? = null

    var toolBar: androidx.appcompat.widget.Toolbar? = null

    private var stateListener = this as DataStateListener
    var specialOfferId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discount_layout)
        specialOfferViewModel = ViewModelProvider(this)[SpecialOfferViewModel::class.java]
        viewDiscountViewModel = ViewModelProvider(this)[ViewDiscountViewModel::class.java]

        initViews()
        retrieveSpecialOfferId()
        val token = Hawk.get<String>(Constants.TOKEN)
        specialOfferId?.let {
            specialOfferViewModel.setStateEvent(SpecialOfferStateEvent.RequestSpecialOfferById(token, it))
            viewDiscountViewModel.setStateEvent(SetViewedDiscountStateEvent.ViewDiscountStateEvent(token, it
            ))
        }

        subscribeObserver()
        actionBar?.hide()
        supportActionBar?.hide()

        toolBar?.navigationIcon = null
        toolBar?.title = "Акция"
    }

    private fun initViews() {
        toolBar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progress_bar)
        titleSpecialOffer = findViewById(R.id.titleTextView)
        bodyTextSpecialOffer = findViewById(R.id.bodyTextView)
        specialOfferImageView = findViewById(R.id.discountImageView)
    }

    private fun setContent(specialOfferDto: SpecialOfferDto?) {
        titleSpecialOffer?.text = specialOfferDto?.title
        bodyTextSpecialOffer?.text = specialOfferDto?.text

        specialOfferImageView?.let {
            Glide.with(this).load(specialOfferDto?.logo).into(it)
        }

        adapterRecyclerView =
            WashAdapterRecyclerView(specialOfferDto?.washes) { model: WashModel ->
                washClicked(model)
            }
        listWashes?.adapter = adapterRecyclerView
        listWashes?.layoutManager = LinearLayoutManager(this)

    }

    private fun washClicked(washModel: WashModel) {

    }

    private fun retrieveSpecialOfferId() {
        specialOfferId = intent?.getStringExtra("stock_id")?.toIntOrNull()
    }

    private fun subscribeObserver() {
        specialOfferViewModel.dataState.observe(this, Observer {
            stateListener.onDataStateChange(it)
            it.data?.let { discountViewState ->
                discountViewState.getContentIfNotHandled()?.let {
                    it.discountResponse?.let {
                        specialOfferViewModel.setDiscountResponse(it)
                    }
                }
            }
        })

        specialOfferViewModel.viewState.observe(this, Observer { viewState ->
            setContent(viewState.discountResponse)
        })
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.loading?.let {
            progressBar?.show(it)
        }
    }
}
