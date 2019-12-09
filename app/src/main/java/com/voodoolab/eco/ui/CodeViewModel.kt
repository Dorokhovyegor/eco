package com.voodoolab.eco.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.CodeRepo
import com.voodoolab.eco.responses.AuthCodeResponse
import com.voodoolab.eco.states.code_state.CodeStateEvent
import com.voodoolab.eco.states.code_state.CodeViewState
import com.voodoolab.eco.utils.AbsentLiveData

class CodeViewModel: ViewModel() {

    private val _stateEvent: MutableLiveData<CodeStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<CodeViewState> = MutableLiveData()

    val viewState: LiveData<CodeViewState>
        get() = _viewState

    val dataState: LiveData<DataState<CodeViewState>> = Transformations
        .switchMap(_stateEvent) {stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: CodeStateEvent): LiveData<DataState<CodeViewState>> {
        when (stateEvent) {
            is CodeStateEvent.RequestCodeEvent -> {
                return CodeRepo.getCode(stateEvent.phoneNumber)
            }
            is CodeStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setCodeResponse(codeResponse: AuthCodeResponse) {
        val update = getCurrentViewStateOrNew()
        update.codeResponse = codeResponse
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): CodeViewState {
        val value = viewState.value?.let {
            it
        } ?: CodeViewState()
        return value
    }

    fun setStateEvent(event: CodeStateEvent) {
        _stateEvent.value = event
    }


}

