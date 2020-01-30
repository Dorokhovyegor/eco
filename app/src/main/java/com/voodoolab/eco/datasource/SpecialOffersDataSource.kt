package com.voodoolab.eco.datasource

import androidx.paging.PageKeyedDataSource
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.SpecialOffersResponse
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat


class SpecialOffersDataSource(val token: String, val city: String?) :
    PageKeyedDataSource<Int, SpecialOfferModel>() {

    val FIRST_PAGE = 1
    val QUANTITY = 5

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, SpecialOfferModel>
    ) {
        RetrofitBuilder.apiService.getSpecialOffers(
            token,
            city,
            FIRST_PAGE.toString(),
            QUANTITY.toString()
        ).enqueue(
            object : retrofit2.Callback<SpecialOffersResponse> {
                override fun onFailure(call: Call<SpecialOffersResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<SpecialOffersResponse>,
                    response: Response<SpecialOffersResponse>
                ) {
                    response.body()?.let { responseList ->
                        val convertResponse = ArrayList<SpecialOfferModel>()
                        responseList.offers?.forEach {
                            convertResponse.add(
                                SpecialOfferModel(
                                    it.id,
                                    it.status,
                                    it.startTime,
                                    convertDate(it.endTime),
                                    it.cashBack,
                                    it.title,
                                    it.body,
                                    it.imageUrl
                                )
                            )
                        }
                        responseList.offers?.let {
                            callback.onResult(convertResponse, null, FIRST_PAGE + 1)
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
        RetrofitBuilder.apiService.getSpecialOffers(
            token,
            city,
            params.key.toString(),
            QUANTITY.toString()
        )
            .enqueue(object : retrofit2.Callback<SpecialOffersResponse> {
                override fun onFailure(call: Call<SpecialOffersResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<SpecialOffersResponse>,
                    response: Response<SpecialOffersResponse>
                ) {
                    val key = params.key + 1
                    response.body()?.let { specialOffersResponse ->

                        val convertResponse = ArrayList<SpecialOfferModel>()
                        specialOffersResponse.offers?.forEach {
                            convertResponse.add(
                                SpecialOfferModel(
                                    it.id,
                                    it.status,
                                    it.startTime,
                                    convertDate(it.endTime),
                                    it.cashBack,
                                    it.title,
                                    it.body,
                                    it.imageUrl
                                )
                            )
                        }
                        specialOffersResponse.offers?.let {
                            callback.onResult(convertResponse, key)
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

    private fun convertDate(dateString: String?): String {
        return try {
            val dateFormatFromServer = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val myDateFormat = SimpleDateFormat("dd MMMM")
            val time = dateFormatFromServer.parse(dateString)?.time
            val normalDate = myDateFormat.format(time)
            normalDate
        } catch (targetException: InvocationTargetException) {
            targetException.printStackTrace()
            "неизвестный формат"
        } catch (parseException: ParseException) {
            parseException.printStackTrace()
            "неизвестный формат"
        }
    }
}