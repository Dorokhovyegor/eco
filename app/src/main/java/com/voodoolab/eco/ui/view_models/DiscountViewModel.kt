package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.DiscountRepo
import com.voodoolab.eco.responses.DiscountResponse
import com.voodoolab.eco.states.discount_state.DiscountStateEvent
import com.voodoolab.eco.states.discount_state.DiscountViewState

class DiscountViewModel : ViewModel(){

    private val _stateEvent: MutableLiveData<DiscountStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<DiscountViewState> = MutableLiveData()

    val viewState: LiveData<DiscountViewState>
        get() = _viewState

    val dataState: LiveData<DataState<DiscountViewState>> = Transformations
        .switchMap(_stateEvent) {stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    private fun handleStateEvent(event: DiscountStateEvent): LiveData<DataState<DiscountViewState>> {
        return when (event) {
            is DiscountStateEvent.RequestDiscountById -> {
                DiscountRepo.getDiscount(event.discountId, event.token)
            }
        }
    }

    fun setDiscountResponse(discountResponse: DiscountResponse) {
        val update = getCurrentViewStateOrNew()
        update.discountResponse = discountResponse
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): DiscountViewState {
        val value = viewState.value?.let {
            it
        } ?: DiscountViewState()
        return value
    }

    fun setStateEvent(event: DiscountStateEvent) {
        _stateEvent.value = event

    }

}