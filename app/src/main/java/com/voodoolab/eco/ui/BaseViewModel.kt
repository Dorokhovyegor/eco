package com.voodoolab.eco.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {

    val TAG = "AppDebug"

    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    /*
    просто геттер для viewState*/
    val viewState: LiveData<ViewState>
        get() = _viewState


    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {event ->
                handleStateEvent(event)
            }
        }

    fun setStateEvent(event: StateEvent) {
        _stateEvent.value = event
    }

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }


    fun getCurrentNewStateOrNew(): ViewState {
        val value = viewState.value?.let {
            it
        } ?: initNewViewState()
        return value
    }

    abstract fun initNewViewState(): ViewState

    abstract fun handleStateEvent(event: StateEvent): LiveData<DataState<ViewState>>



}
