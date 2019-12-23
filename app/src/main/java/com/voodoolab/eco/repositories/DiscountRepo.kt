package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.DiscountResponse
import com.voodoolab.eco.states.discount_state.DiscountViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object DiscountRepo {

    fun getDiscount(discountID: Int, token: String): LiveData<DataState<DiscountViewState>> {
        return object: NetworkBoundResource<DiscountResponse, DiscountViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<DiscountResponse>) {
                result.value = DataState.data(
                    data = DiscountViewState(
                        discountResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<DiscountResponse>> {
                return RetrofitBuilder.apiService.getSpeciaOfferById(token, discountID)
            }
        }.asLiveData()
    }

}