package com.voodoolab.eco.datasource

import androidx.paging.PageKeyedDataSource
import com.voodoolab.eco.models.TransactionData
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.TransactionResponse
import com.voodoolab.eco.utils.Constants
import retrofit2.Call
import retrofit2.Response

class TransactionDataSource(val token: String, val map: Map<String, Any?>?) :
    PageKeyedDataSource<Int, TransactionData>() {

    val FIRST_PAGE = 1
    val QUANTITY = 10

    private var paramsList: ArrayList<String>? = ArrayList()
    private var filterTo: String? = null
    private var filterFrom: String? = null

    init {
        map?.forEach { item ->
            if (item.key == Constants.FILTER_REPLENISH_ONLINE ||
                item.key == Constants.FILTER_REPLENISH_OFFLINE ||
                item.key == Constants.FILTER_WASTE ||
                item.key == Constants.FILTER_MONTHBONUS ||
                item.key == Constants.FILTER_CASHBACK
            ) {
                if (item.value is Boolean && item.value == true) {
                    paramsList?.add(item.key)
                }
            }
        }

        map?.let {
            if (it.containsKey(Constants.FILTER_PERIOD_FROM)) {
                filterFrom = it[Constants.FILTER_PERIOD_FROM] as String?
            }
            if (it.containsKey(Constants.FILTER_PERIOD_TO)) {
                filterTo = it[Constants.FILTER_PERIOD_TO] as String?
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, TransactionData>
    ) {
        RetrofitBuilder.apiService.getOperations(
            token = "Bearer $token",
            accept = "application/json",
            page = FIRST_PAGE.toString(),
            qty = QUANTITY.toString(),
            types = paramsList,
            from = filterFrom,
            to = filterTo
        ).enqueue(object : retrofit2.Callback<TransactionResponse> {
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    println("DEBUG оп!")
                    response.body()?.let { transactionsResponse ->
                        transactionsResponse.data?.let {
                            if (it.size != 0) {
                                it.add(
                                    0,
                                    TransactionData(
                                        null,
                                        null,
                                        Constants.TRANSACTION_HEADER_TYPE,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }
                            callback.onResult(it, null, FIRST_PAGE + 1)
                        }
                    }
                }
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionData>) {

        val call = RetrofitBuilder.apiService.getOperations(
            token = "Bearer $token",
            accept = "application/json",
            page = params.key.toString(),
            qty = QUANTITY.toString(),
            to = filterTo,
            from = filterFrom,
            types = paramsList
        )

        call.enqueue(object : retrofit2.Callback<TransactionResponse> {
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {}
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                val key = params.key + 1
                response.body()?.let { apartmentResponse ->
                    apartmentResponse.data?.let {
                        callback.onResult(it, key)
                    }
                }
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionData>) {

    }
}
