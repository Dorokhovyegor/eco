package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.repositories.PaymentRepo
import com.voodoolab.eco.states.payment_state.PaymentStateEvent
import com.voodoolab.eco.states.payment_state.PaymentViewState
import com.voodoolab.eco.utils.AbsentLiveData

class PaymentViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<PaymentStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<PaymentViewState> = MutableLiveData()

    val viewState: LiveData<PaymentViewState>
        get() = _viewState

    val dataState: LiveData<DataState<PaymentViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: PaymentStateEvent): LiveData<DataState<PaymentViewState>> {
        return when (stateEvent) {
            is PaymentStateEvent.RequestPaymentWebUrl -> {
                PaymentRepo.requestPaymentWebUrl(stateEvent.token, stateEvent.amount)
            }
            is PaymentStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun setWebHtmlResponse(response: String) {
        val update = getCurrentViewStateOrNew()
        update.paymentHtml = response
        _viewState.value = update
    }

    fun setStateEvent(event: PaymentStateEvent) {
        _stateEvent.value = event
    }

    fun getCurrentViewStateOrNew(): PaymentViewState {
        val value = viewState.value?.let {
            it
        } ?: PaymentViewState()
        return value
    }

}