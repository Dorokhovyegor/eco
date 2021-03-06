package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.repositories.CitiesRepo
import com.voodoolab.eco.repositories.UserRepo
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.states.user_state.UserViewState

class UserInfoViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<UserStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<UserViewState> = MutableLiveData()

    val viewState: LiveData<UserViewState>
        get() = _viewState

    val dataState: LiveData<DataState<UserViewState>> = Transformations
        .switchMap(_stateEvent) {stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: UserStateEvent): LiveData<DataState<UserViewState>> {
        return when (stateEvent) {
            is UserStateEvent.RequestUserInfo -> {
                UserRepo.getUserInfo(stateEvent.token)
            }
            is UserStateEvent.SetCityEvent -> {
                CitiesRepo.setCity(stateEvent.token, stateEvent.cityName)
            }
        }
    }

    fun setUserInfo(userResponse: ClearUserModel?) {
        val update = getCurentViewStateOrNew()
        update.userResponse = userResponse
        _viewState.value = update
    }

    fun getCurentViewStateOrNew(): UserViewState {
        val value = viewState.value?.let {
            it
        }?: UserViewState()
        return value
    }

    fun setStateEvent(event: UserStateEvent) {
        _stateEvent.value = event
    }

}