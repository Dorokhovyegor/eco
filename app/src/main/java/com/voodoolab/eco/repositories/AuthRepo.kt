package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.LoginResponse
import com.voodoolab.eco.states.auth_state.AuthViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object AuthRepo {
    fun login(phone: String?, code: String?): LiveData<DataState<AuthViewState>> {
        return object: NetworkBoundResource<LoginResponse, AuthViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                result.value = DataState.data(
                    data = AuthViewState(
                        loginResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return RetrofitBuilder.apiService.login(phone.toString(),code.toString())
            }
        }.asLiveData()
    }
}