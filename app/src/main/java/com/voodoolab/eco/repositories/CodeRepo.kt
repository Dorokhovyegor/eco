package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.AuthCodeResponse
import com.voodoolab.eco.states.code_state.CodeViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object CodeRepo {
    fun getCode(phone: String?): LiveData<DataState<CodeViewState>> {
        return object : NetworkBoundResource<AuthCodeResponse, CodeViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<AuthCodeResponse>) {
                result.value = DataState.data(
                    data = CodeViewState(
                        codeResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<AuthCodeResponse>> {
                return RetrofitBuilder.apiService.requestCode(phone.toString())
            }
        }.asLiveData()
    }
}