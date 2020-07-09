package com.voodoolab.eco.ui.specialoffers.viewmodel

import androidx.lifecycle.LiveData
import com.voodoolab.eco.repositories.discounts.DiscountRepo
import com.voodoolab.eco.session.SessionManager
import com.voodoolab.eco.states.discount_state.SpecialOfferStateEvent
import com.voodoolab.eco.states.discount_state.SpecialOfferViewState
import com.voodoolab.eco.ui.BaseViewModel
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.utils.AbsentLiveData
import javax.inject.Inject

class SpecialOfferViewModel
@Inject
constructor(
    private val discountRepo: DiscountRepo,
    private val sessionManager: SessionManager
) : BaseViewModel<SpecialOfferStateEvent, SpecialOfferViewState>() {

    override fun initNewViewState(): SpecialOfferViewState = SpecialOfferViewState()

    override fun handleStateEvent(event: SpecialOfferStateEvent): LiveData<DataState<SpecialOfferViewState>> {
        when (event) {
            is SpecialOfferStateEvent.RequestSpecialOfferById -> {
                return sessionManager.cashedToken.value?.let { authToken ->
                    discountRepo.getSpecialOffer(
                        authToken.token!!,
                        event.discountId
                    )
                } ?: AbsentLiveData.create()
            }
            is SpecialOfferStateEvent.RequestSpecialOfferListByPage -> {
                return sessionManager.cashedToken.value?.let { authToken ->
                    discountRepo.getSpecialOfferList(
                        authToken.token!!,
                        city = getCity(),
                        page = getPage(),
                        qty = 15
                    )
                }?: AbsentLiveData.create()
            }
            is SpecialOfferStateEvent.None -> {
                return object : LiveData<DataState<SpecialOfferViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    fun cancelActiveJobs() {
        discountRepo.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(SpecialOfferStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}