package com.voodoolab.eco.states.view_discounts_state

sealed class SetViewedDiscountStateEvent {
    class ViewDiscountStateEvent(
        val token: String,
        val specialOfferId: Int
    ) : SetViewedDiscountStateEvent()

}