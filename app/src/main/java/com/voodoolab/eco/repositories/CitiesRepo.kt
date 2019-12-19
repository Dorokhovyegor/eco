package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.models.CityModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.states.cities_state.CitiesViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object CitiesRepo{

    fun requestListCities(): LiveData<DataState<CitiesViewState>> {
        return object : NetworkBoundResource<List<CityModel>, CitiesViewState>() {

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<List<CityModel>>) {
                result.value = DataState.data(
                    data = CitiesViewState(
                        citiesResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<List<CityModel>>> {
                println("DEBUG: создал запрос")
                return RetrofitBuilder.apiService.getAllCities( "application/json")
            }
        }.asLiveData()
    }

}