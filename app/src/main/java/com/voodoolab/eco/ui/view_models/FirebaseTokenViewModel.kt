package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.repositories.FirebaseTokenRepo
import com.voodoolab.eco.responses.UpdateTokenResponse
import com.voodoolab.eco.states.firebase_token_state.UpdateTokenFireBaseStateEvent
import com.voodoolab.eco.states.firebase_token_state.UpdateTokenFireBaseViewState

class FirebaseTokenViewModel: ViewModel() {

    private val _updateTokenStateEvent: MutableLiveData<UpdateTokenFireBaseStateEvent> =
        MutableLiveData()

    private val _updateTokenViewState: MutableLiveData<UpdateTokenFireBaseViewState> =
        MutableLiveData()

    val viewState: LiveData<UpdateTokenFireBaseViewState>
        get() = _updateTokenViewState

    val dataState: LiveData<DataState<UpdateTokenFireBaseViewState>> = Transformations
        .switchMap(_updateTokenStateEvent) {
            handleStateEvent(it)
        }

    fun handleStateEvent(stateEvent: UpdateTokenFireBaseStateEvent): LiveData<DataState<UpdateTokenFireBaseViewState>> {
        return when (stateEvent) {
            is UpdateTokenFireBaseStateEvent.UpdateTokenEvent -> {
                FirebaseTokenRepo
                    .updateToken(stateEvent.appToken, stateEvent.firebaseToken)
            }
        }
    }

    fun setTokenResponse(tokenResponse: UpdateTokenResponse) {
        val update = getCurrentViewStateOrNew()
        update.updateTokenResponse = tokenResponse
        _updateTokenViewState.value = update
    }

    fun getCurrentViewStateOrNew(): UpdateTokenFireBaseViewState {
        val value = viewState.value?.let {
            it
        }?: UpdateTokenFireBaseViewState()
        return value
    }

    fun setStateEvent(event: UpdateTokenFireBaseStateEvent) {
        _updateTokenStateEvent.value = event
    }
}