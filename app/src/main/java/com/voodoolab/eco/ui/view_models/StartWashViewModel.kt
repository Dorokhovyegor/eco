package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.repositories.StartWashRepo
import com.voodoolab.eco.states.startwash_state.StartWashStateEvent
import com.voodoolab.eco.states.startwash_state.StartWashViewState
import com.voodoolab.eco.utils.AbsentLiveData

class StartWashViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<StartWashStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<StartWashViewState> = MutableLiveData()

    val viewState: LiveData<StartWashViewState>
        get() = _viewState

    val dataState: LiveData<DataState<StartWashViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: StartWashStateEvent): LiveData<DataState<StartWashViewState>> {
        return when (stateEvent) {
            is StartWashStateEvent.StartWashViaCode -> {
                StartWashRepo.startWashViaCode(stateEvent.token, stateEvent.code)
            }
            is StartWashStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun setResponse(response: String) {
        val update = getCurrentViewStateOrNew()
        update.message = response
        _viewState.value = update
    }

    fun setStateEvent(event: StartWashStateEvent) {
        _stateEvent.value = event
    }

    fun getCurrentViewStateOrNew(): StartWashViewState {
        val value = viewState.value?.let {
            it
        } ?: StartWashViewState()
        return value
    }
}