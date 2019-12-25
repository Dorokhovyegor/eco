package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.models.CityModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.CitiesRepo
import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.responses.UpdateCityResponse
import com.voodoolab.eco.states.cities_state.CitiesStateEvent
import com.voodoolab.eco.states.cities_state.CitiesViewState
import com.voodoolab.eco.utils.AbsentLiveData

class CitiesViewModels : ViewModel() {

    private val _citiesStateEvent: MutableLiveData<CitiesStateEvent> = MutableLiveData()
    private val _citiesViewState: MutableLiveData<CitiesViewState> = MutableLiveData()

    val viewState: LiveData<CitiesViewState>
        get() = _citiesViewState

    val dataState: LiveData<DataState<CitiesViewState>> = Transformations
        .switchMap(_citiesStateEvent) {stateEvent->
            stateEvent?.let {
                // todo handle this event
                handleStateEvent(it)
            }
        }

    private fun handleStateEvent(stateEvent: CitiesStateEvent): LiveData<DataState<CitiesViewState>> {
        return when (stateEvent) {
            is CitiesStateEvent.RequestCityList -> {
                CitiesRepo.requestListCities()
            }
            is CitiesStateEvent.UpdateCity -> {
                CitiesRepo.setCity(stateEvent.token, stateEvent.city)
            }
            is CitiesStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun updateCityResonse(citiesResponse: CitiesResponse?, updateCityResponse: UpdateCityResponse?) {
        val update = getCurrentViewStateOrNew()
        update.citiesResponse = citiesResponse
        update.updateCityResponse = updateCityResponse
        _citiesViewState.value = update
    }

    fun getCurrentViewStateOrNew(): CitiesViewState {
        val value = viewState.value?.let {
            it
        }?: CitiesViewState()
        return value
    }

    fun setStateEvent(event: CitiesStateEvent) {
        _citiesStateEvent.value = event
    }
}