package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.user_state.UserViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object UserRepo {
    fun getUserInfo(token: String): LiveData<DataState<UserViewState>> {
        return object : NetworkBoundResource<UserInfoResponse, UserViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<UserInfoResponse>) {
                result.value = DataState.data(
                    data = UserViewState(
                        userResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UserInfoResponse>> {
                return RetrofitBuilder.apiService.getUserInfo("Bearer " + token, "application/json")
            }
        }.asLiveData()
    }


}