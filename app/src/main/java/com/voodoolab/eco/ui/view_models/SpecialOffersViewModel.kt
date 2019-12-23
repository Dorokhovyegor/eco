package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.voodoolab.eco.datasource.SpecialOffersDataSource
import com.voodoolab.eco.datasource.SpecialOffersDataSourceFactory
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.models.TransactionData

class SpecialOffersViewModel : ViewModel() {

    var offersPagedList: LiveData<PagedList<SpecialOfferModel>>? = null
    private var liveDataSource: LiveData<SpecialOffersDataSource>? = null

    fun init(token: String, city: String?, emptyListInterface: EmptyListInterface?) {

        val factory = SpecialOffersDataSourceFactory(token, city)
        liveDataSource = factory.offersLiveDataSource

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(5)
            .build()

        offersPagedList = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<SpecialOfferModel>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    emptyListInterface?.setEmptyState()
                }
            })
            .build()
    }
}