package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.models.SetDiscountViewedResponse
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.ViewDiscountRepo
import com.voodoolab.eco.states.view_discounts_state.SetViewedDiscountStateEvent
import com.voodoolab.eco.states.view_discounts_state.ViewDiscountStateView

class ViewDiscountViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<SetViewedDiscountStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<ViewDiscountStateView> = MutableLiveData()

    val viewState: LiveData<ViewDiscountStateView>
        get() = _viewState

    val dataState: LiveData<DataState<ViewDiscountStateView>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }

        }

    private fun handleStateEvent(event: SetViewedDiscountStateEvent): LiveData<DataState<ViewDiscountStateView>> {
        when (event) {
            is SetViewedDiscountStateEvent.ViewDiscountStateEvent -> {
                return ViewDiscountRepo.setViewed(event.token, event.specialOfferId)
            }
        }
    }

    fun setResponse(response: SetDiscountViewedResponse) {
        val update = getCurrentViewStateOrNew()
        update.response = response
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): ViewDiscountStateView {
        val value = viewState.value?.let {
            it
        } ?: ViewDiscountStateView()
        return value
    }

    fun setStateEvent(event: SetViewedDiscountStateEvent) {
        _stateEvent.value = event
    }

}