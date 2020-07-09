package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.models.WashStartResponse
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.states.startwash_state.StartWashViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object StartWashRepo {
    fun startWashViaCode(token: String, code: String): LiveData<DataState<StartWashViewState>> {
        return object : NetworkBoundResource<WashStartResponse, StartWashViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<WashStartResponse>) {
                if (response.body.status == "ok") {
                    result.value = DataState.data("Мойка началась")
                } else {
                    result.value =    DataState.data("Не удалось начать")
                }
            }
            override fun createCall(): LiveData<GenericApiResponse<WashStartResponse>> {
                return RetrofitBuilder.apiService.startWash("Bearer ${token}", code)
            }
        }.asLiveData()
    }
}