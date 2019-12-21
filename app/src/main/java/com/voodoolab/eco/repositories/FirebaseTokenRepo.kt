package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.UpdateTokenResponse
import com.voodoolab.eco.states.firebase_token_state.UpdateTokenFireBaseViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object FirebaseTokenRepo{

    fun updateToken(
        appToken: String,
        fireBaseToken: String
    ): LiveData<DataState<UpdateTokenFireBaseViewState>> {
        return object : NetworkBoundResource<UpdateTokenResponse, UpdateTokenFireBaseViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<UpdateTokenResponse>) {
                result.value = DataState.data(
                    data = UpdateTokenFireBaseViewState(
                        updateTokenResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UpdateTokenResponse>> {
                return RetrofitBuilder.apiService.sendTokenToServer(appToken, fireBaseToken)
            }
        }.asLiveData()
    }

}