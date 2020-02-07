package com.voodoolab.eco.datasource

import androidx.lifecycle.MutableLiveData
import com.voodoolab.eco.models.SpecialOfferModel

class SpecialOffersDataSourceFactory(
    val token: String,
    val city: String?
) : androidx.paging.DataSource.Factory<Int, SpecialOfferModel>() {

    val offersLiveDataSource = MutableLiveData<SpecialOffersDataSource>()

    override fun create(): androidx.paging.DataSource<Int, SpecialOfferModel> {
            val offersDataSource = SpecialOffersDataSource(token, city)
            offersLiveDataSource.postValue(offersDataSource)
            return offersDataSource
    }
}