package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.ListObjectResponse
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ListObjectViewState
import com.voodoolab.eco.states.object_state.ObjectViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object ObjectRepo {

    fun getObjectInfo(token: String, id: Int): LiveData<DataState<ObjectViewState>> {
        return object: NetworkBoundResource<ObjectResponse, ObjectViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<ObjectResponse>) {
                result.value = DataState.data(
                    data = ObjectViewState(
                        objectResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ObjectResponse>> {
               return RetrofitBuilder.apiService.getObjectById(token, "application/json", id)
            }
        }.asLiveData()
    }

    fun getObjectList(token: String): LiveData<DataState<ListObjectViewState>> {
        return object: NetworkBoundResource<ListObjectResponse, ListObjectViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<ListObjectResponse>) {
                result.value = DataState.data(
                    data = ListObjectViewState(
                        listObjectResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ListObjectResponse>> {
                return RetrofitBuilder.apiService.getAllObjects(token, "application/json")
            }
        }.asLiveData()
    }

}