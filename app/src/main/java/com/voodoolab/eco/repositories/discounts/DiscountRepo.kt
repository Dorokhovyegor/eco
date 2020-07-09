package com.voodoolab.eco.repositories.discounts

import androidx.lifecycle.LiveData
import com.voodoolab.eco.api.ApiService
import com.voodoolab.eco.api.specialofferdto.ListSpecialOfferDto
import com.voodoolab.eco.api.specialofferdto.SpecialOfferDto
import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.repositories.JobManager
import com.voodoolab.eco.repositories.NetworkBoundResource
import com.voodoolab.eco.session.SessionManager
import com.voodoolab.eco.states.discount_state.SpecialOfferFields
import com.voodoolab.eco.states.discount_state.SpecialOfferListFields
import com.voodoolab.eco.states.discount_state.SpecialOfferViewState
import com.voodoolab.eco.ui.DataState
import com.voodoolab.eco.utils.AbsentLiveData
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

class DiscountRepo
@Inject
constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : JobManager("DiscountRepo") {

    fun getSpecialOffer(
        token: String,
        discountID: Int
    ): LiveData<DataState<SpecialOfferViewState>> {
        return object : NetworkBoundResource<SpecialOfferDto, Any, SpecialOfferViewState>(
            sessionManager.isConnectedToTheInternet(), true, true, false
        ) {

            override suspend fun createCasheRequestAndReturn() {
                // not applicable
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<SpecialOfferDto>) {
                onCompleteJob(
                    DataState.data(
                        SpecialOfferViewState(
                            specialOfferFields = SpecialOfferFields(
                                response.body.title,
                                response.body.text,
                                response.body.logo,
                                response.body.washes
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<SpecialOfferDto>> {
                return apiService.getSpecialOfferById(token, discountID)
            }

            override fun loadFromCache(): LiveData<SpecialOfferViewState> {
                // not applicable
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                // not applicable
            }

            override fun setJob(job: Job) {
                addJob("getSpecialOffer", job)
            }
        }.asLiveData()
    }

    fun getSpecialOfferList(
        token: String,
        city: String,
        page: Int,
        qty: Int
    ): LiveData<DataState<SpecialOfferViewState>> {
        return object : NetworkBoundResource<ListSpecialOfferDto, Any, SpecialOfferViewState>(
            sessionManager.isConnectedToTheInternet(), true, true, false
        ) {
            override suspend fun createCasheRequestAndReturn() {
                // not applicable
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListSpecialOfferDto>) {
                val specialOfferList: ArrayList<SpecialOfferModel> = ArrayList()
                response.body.offers?.let { offers ->
                    specialOfferList.addAll(offers)
                }

                onCompleteJob(DataState.data(
                    SpecialOfferViewState(
                        specialOfferList = SpecialOfferListFields(
                            list = specialOfferList
                        )
                    )
                ))
            }

            override fun createCall(): LiveData<GenericApiResponse<ListSpecialOfferDto>> {
                return apiService.getSpecialOffers(token, city, page, qty)
            }

            override fun loadFromCache(): LiveData<SpecialOfferViewState> {
                // not applicable
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                // not applicable
            }

            override fun setJob(job: Job) {
                addJob("getSpecialOfferList", job)
            }
        }.asLiveData()
    }
}