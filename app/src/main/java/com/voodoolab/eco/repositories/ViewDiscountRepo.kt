package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.models.SetDiscountViewedResponse
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.states.view_discounts_state.ViewDiscountStateView
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object ViewDiscountRepo {
    fun setViewed(token: String, id: Int): LiveData<DataState<ViewDiscountStateView>> {
        return object : NetworkBoundResource<SetDiscountViewedResponse, ViewDiscountStateView>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<SetDiscountViewedResponse>) {
                result.value = DataState.data(
                    data = ViewDiscountStateView(
                        response = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<SetDiscountViewedResponse>> {
                return RetrofitBuilder.apiService.setViewedDiscount(token, id)
            }
        }.asLiveData()
    }
}