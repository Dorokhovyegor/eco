package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.repositories.UserRepo
import com.voodoolab.eco.responses.LogoutResponse
import com.voodoolab.eco.states.logout_state.LogoutStateEvent
import com.voodoolab.eco.states.logout_state.LogoutViewState

class LogoutViewModel : ViewModel() {
    private val _stateEvent: MutableLiveData<LogoutStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<LogoutViewState> = MutableLiveData()

    val viewState: LiveData<LogoutViewState>
        get() = _viewState

    val dataState: LiveData<DataState<LogoutViewState>> = Transformations
        .switchMap(_stateEvent) {stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: LogoutStateEvent): LiveData<DataState<LogoutViewState>> {
        when (stateEvent) {
            is LogoutStateEvent.LogoutEvent -> {
                return UserRepo.logout(stateEvent.token)
            }
        }
    }

    fun setLogoutResponse(loginResponse: LogoutResponse) {
        val update = getCurrentViewStateOrNew()
        update.logoutResponse = loginResponse
        _viewState.value = update
    }


    fun getCurrentViewStateOrNew(): LogoutViewState {
        val value = viewState.value?.let {
            it
        } ?: LogoutViewState()
        return value
    }

    fun setStateEvent(event: LogoutStateEvent) {
        _stateEvent.value = event
    }

}