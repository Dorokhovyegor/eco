package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.UrlResponse
import com.voodoolab.eco.states.payment_state.PaymentViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object PaymentRepo {
    fun requestPaymentWebUrl(token: String, amount: Int): LiveData<DataState<PaymentViewState>> {
        return object : NetworkBoundResource<UrlResponse, PaymentViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<UrlResponse>) {
                result.value = DataState.data(
                    data = PaymentViewState(
                        paymentHtml = response.body.data.url
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UrlResponse>> {
                return RetrofitBuilder.apiService.getHtmlFromServer("Bearer $token", amount)
            }
        }.asLiveData()
    }
}