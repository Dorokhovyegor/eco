package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.LogoutResponse
import com.voodoolab.eco.responses.UpdateNameResponse
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.logout_state.LogoutViewState
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

    fun updateUserName(token: String, name: String): LiveData<DataState<UserViewState>> {
        return object : NetworkBoundResource<UpdateNameResponse, UserViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<UpdateNameResponse>) {
                result.value = DataState.data(
                    data = UserViewState(
                        updateNameResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UpdateNameResponse>> {
                return RetrofitBuilder.apiService.setNewName("Bearer " + token, name)
            }
        }.asLiveData()
    }

    fun logout(token: String): LiveData<DataState<LogoutViewState>>{
        return object: NetworkBoundResource<LogoutResponse, LogoutViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<LogoutResponse>) {
                result.value = DataState.data(
                    data = LogoutViewState(
                        logoutResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LogoutResponse>> {
                return RetrofitBuilder.apiService.logout("Bearer " + token)
            }
        }.asLiveData()
    }
}