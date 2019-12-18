package com.voodoolab.eco.helper_fragments.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.ObjectRepo
import com.voodoolab.eco.responses.ListObjectResponse
import com.voodoolab.eco.responses.ObjectResponse
import com.voodoolab.eco.states.object_state.ListObjectStateEvent
import com.voodoolab.eco.states.object_state.ListObjectViewState
import com.voodoolab.eco.states.object_state.ObjectStateEvent
import com.voodoolab.eco.states.object_state.ObjectViewState
import com.voodoolab.eco.utils.AbsentLiveData

class ObjectInfoViewModel : ViewModel() {

    private val _stateEventForSingleObject: MutableLiveData<ObjectStateEvent> = MutableLiveData()
    private val _viewStateForSingleObject: MutableLiveData<ObjectViewState> = MutableLiveData()

    val viewStateObject: LiveData<ObjectViewState>
        get() = _viewStateForSingleObject

    val dataStateObject: LiveData<DataState<ObjectViewState>> = Transformations
        .switchMap(_stateEventForSingleObject) { stateEvent ->
            stateEvent?.let {
                handleStateEventForSingleObject(it)
            }
        }

    private val _stateEventForList: MutableLiveData<ListObjectStateEvent> = MutableLiveData()
    private val _viewStateForList: MutableLiveData<ListObjectViewState> = MutableLiveData()

    val viewStateListObject: LiveData<ListObjectViewState>
        get() = _viewStateForList

    val dataStateListObject: LiveData<DataState<ListObjectViewState>> = Transformations
        .switchMap(_stateEventForList) {stateEvent ->
            stateEvent?.let {
                handleStateEventForList(it)
            }
        }

    private fun handleStateEventForSingleObject(stateEvent: ObjectStateEvent): LiveData<DataState<ObjectViewState>> {
        return when (stateEvent) {
            is ObjectStateEvent.RequestObjectEvent -> {
                ObjectRepo.getObjectInfo(stateEvent.token, stateEvent.id)
            }
            is ObjectStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    private fun handleStateEventForList(stateEvent: ListObjectStateEvent): LiveData<DataState<ListObjectViewState>> {
        return when(stateEvent) {
            is ListObjectStateEvent.RequestAllObjectEvent -> {
                ObjectRepo.getObjectList(stateEvent.token)
            }
            is ListObjectStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun setListResponse(listObjectResponse: ListObjectResponse) {
        val update = getCurrentViewStateOrNewForList()
        update.listObjectResponse = listObjectResponse
        _viewStateForList.value = update
    }

    fun setObjectResponse(objectResponse: ObjectResponse) {
        val update = getCurrentViewStateOrNew()
        update.objectResponse = objectResponse
        _viewStateForSingleObject.value = update
    }

    fun getCurrentViewStateOrNewForList(): ListObjectViewState {
        val value = viewStateListObject.value?.let {
            it
        } ?: ListObjectViewState()
        return value
    }

    fun getCurrentViewStateOrNew(): ObjectViewState {
        val value = viewStateObject.value?.let {
            it
        } ?: ObjectViewState()
        return value
    }

    fun setStateEventForObject(event: ObjectStateEvent) {
        _stateEventForSingleObject.value = event
    }

    fun setStateEventForListObject(event: ListObjectStateEvent) {
        _stateEventForList.value = event
    }

}