package com.voodoolab.eco.datasource

import androidx.paging.PageKeyedDataSource
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.SpecialOffersResponse
import retrofit2.Call
import retrofit2.Response

class SpecialOffersDataSource(val token: String, val city: String?) :
    PageKeyedDataSource<Int, SpecialOfferModel>() {

    val FIRST_PAGE = 1
    val QUANTITY = 5

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, SpecialOfferModel>
    ) {
        RetrofitBuilder.apiService.getSpecialOffers(token, city, FIRST_PAGE.toString(), QUANTITY.toString()).enqueue(
            object : retrofit2.Callback<SpecialOffersResponse> {
                override fun onFailure(call: Call<SpecialOffersResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<SpecialOffersResponse>,
                    response: Response<SpecialOffersResponse>
                ) {
                    response.body()?.let { responseList ->
                        responseList.offers?.let {
                            callback.onResult(it, null, FIRST_PAGE + 1)
                        }
                    }
                }
            }
        )
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, SpecialOfferModel>
    ) {
        RetrofitBuilder.apiService.getSpecialOffers(token, city, params.key.toString(), QUANTITY.toString())
            .enqueue( object: retrofit2.Callback<SpecialOffersResponse> {
                override fun onFailure(call: Call<SpecialOffersResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<SpecialOffersResponse>,
                    response: Response<SpecialOffersResponse>
                ) {
                    val key = params.key + 1
                    response.body()?.let { specialOffersResponse ->
                        specialOffersResponse.offers?.let {
                            callback.onResult(it, key)
                        }
                    }
                }
            })

    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, SpecialOfferModel>
    ) {
    }
}