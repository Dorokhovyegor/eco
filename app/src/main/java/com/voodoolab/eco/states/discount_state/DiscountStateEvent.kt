package com.voodoolab.eco.states.discount_state

sealed class DiscountStateEvent {

    class RequestDiscountById(
        val token: String,
        val discountId: Int
    ): DiscountStateEvent()

}