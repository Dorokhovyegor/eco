package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.voodoolab.eco.datasource.SpecialOffersDataSourceFactory
import com.voodoolab.eco.interfaces.EmptyListInterface
import com.voodoolab.eco.models.SpecialOfferModel

class SpecialOffersViewModel : ViewModel() {

    var offersPagedList: LiveData<PagedList<SpecialOfferModel>>? = null
    var city: String? = null

    private fun buildPagedList(
        paramCity: String?,
        token: String,
        emptyListInterface: EmptyListInterface?
    ): LiveData<PagedList<SpecialOfferModel>> {

        println("DEBUG: произошел билд")
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        return LivePagedListBuilder(SpecialOffersDataSourceFactory(token, paramCity), config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<SpecialOfferModel>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    emptyListInterface?.setEmptyState()
                }

                override fun onItemAtEndLoaded(itemAtEnd: SpecialOfferModel) {
                    super.onItemAtEndLoaded(itemAtEnd)
                    emptyListInterface?.lastItemLoaded()
                }

                override fun onItemAtFrontLoaded(itemAtFront: SpecialOfferModel) {
                    super.onItemAtFrontLoaded(itemAtFront)
                    emptyListInterface?.firstItemLoaded()
                }
            }).build()
    }

    fun replaceSubscription(
        lifecycleOwner: LifecycleOwner,
        city: String?,
        token: String,
        emptyListInterface: EmptyListInterface?
    ) {
        offersPagedList?.removeObservers(lifecycleOwner)
        offersPagedList = buildPagedList(city, token, emptyListInterface)
    }

}