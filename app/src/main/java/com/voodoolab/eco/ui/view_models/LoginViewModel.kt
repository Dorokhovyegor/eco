package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.AuthRepo
import com.voodoolab.eco.responses.LoginResponse
import com.voodoolab.eco.states.auth_state.AuthStateEvent
import com.voodoolab.eco.states.auth_state.AuthViewState
import com.voodoolab.eco.utils.AbsentLiveData

class LoginViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<AuthStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<AuthViewState> = MutableLiveData()

    val viewState: LiveData<AuthViewState>
        get() = _viewState

    val dataState: LiveData<DataState<AuthViewState>> = Transformations
        .switchMap(_stateEvent) {stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (stateEvent) {
            is AuthStateEvent.LoginEvent -> {
                return AuthRepo.login(stateEvent.phoneNumber, stateEvent.code)
            }
            is AuthStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setLoginResponse(loginResponse: LoginResponse) {
        val update = getCurrentViewStateOrNew()
        update.loginResponse = loginResponse
        _viewState.value = update
    }


    fun getCurrentViewStateOrNew(): AuthViewState {
        val value = viewState.value?.let {
            it
        } ?: AuthViewState()
        return value
    }

    fun setStateEvent(event: AuthStateEvent) {
        _stateEvent.value = event
    }

}