package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.ReportResponse
import com.voodoolab.eco.states.report_state.ReportViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object ReportRepo {

    fun sendReport(
        tokenApp: String,
        operationId: Int?,
        text: String?,
        rating: Double?
    ): LiveData<DataState<ReportViewState>> {
        return object : NetworkBoundResource<ReportResponse, ReportViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<ReportResponse>) {
                result.value = DataState.data(
                    data = ReportViewState(
                        reportResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ReportResponse>> {

                val controllerSum = rating?.div(0.5)
                if (controllerSum?.toInt()?.rem(2) == 0) { // тут отправляем INT
                    return RetrofitBuilder.apiService.sentReport(tokenApp, operationId, text, rating.toInt())
                } else { // а тут double
                    return RetrofitBuilder.apiService.sentReport(tokenApp, operationId, text, rating)
                }
            }

        }.asLiveData()
    }

}