package com.voodoolab.eco.datasource

import androidx.paging.PageKeyedDataSource
import com.voodoolab.eco.models.TransactionData
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.TransactionResponse
import retrofit2.Call
import retrofit2.Response

class TransactionDataSource(val token: String): PageKeyedDataSource<Int, TransactionData>() {

    val FIRST_PAGE = 1
    val QUANTITY = 5

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, TransactionData>
    ) {
        RetrofitBuilder.apiService.getOperations(token, FIRST_PAGE,QUANTITY).enqueue( object: retrofit2.Callback<TransactionResponse> {
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) { }
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                response.body()?.let { transactionsResponse ->
                    callback.onResult(transactionsResponse.data, null, FIRST_PAGE + 1)
                }
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionData>) {
        val call = RetrofitBuilder.apiService.getOperations(token, params.key, QUANTITY)

        call.enqueue(object: retrofit2.Callback<TransactionResponse> {
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {}
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                val key = params.key + 1
                response.body()?.let { apartmentResponse ->
                    callback.onResult(apartmentResponse.data, key)

                }
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionData>) {

    }
}
