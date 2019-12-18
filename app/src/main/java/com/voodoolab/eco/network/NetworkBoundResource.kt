package com.voodoolab.eco.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.voodoolab.eco.utils.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class NetworkBoundResource<ResponseObject, ViewStateType> {

    protected val result = MediatorLiveData<DataState<ViewStateType>>()

    init {
        // как только юзаем эту оболочку, значит мы что-то используем для оборачивания запроса и сразу говорим, что состояние загрузки
        result.value = DataState.loading(true)
        GlobalScope.launch(IO) {
            withContext(Main) {
                val apiResponse = createCall()
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)
                    handleNetworkCall(response)
                }
            }
        }
    }

    fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiOtherError -> {
                onReturnError(response.errorMessage)
            }
            is ApiEmptyResponse -> {
                onReturnError("code 204 Returned nothing")
            }
        }
    }

    fun onReturnError(message: String) {
        result.value = DataState.error(message)
    }

    abstract fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

}