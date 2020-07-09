package com.voodoolab.eco.states.discount_state

sealed class SpecialOfferStateEvent {

    class RequestSpecialOfferById(
        val token: String,
        val discountId: Int
    ) : SpecialOfferStateEvent()

    class RequestSpecialOfferListByPage(
        val token: String,
        val city: String
        val page: Int,
        val qty: Int
    ) : SpecialOfferStateEvent()

    class None : SpecialOfferStateEvent()

}