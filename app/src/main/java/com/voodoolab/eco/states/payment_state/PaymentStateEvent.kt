package com.voodoolab.eco.states.payment_state

sealed class PaymentStateEvent() {
    class RequestPaymentWebUrl(val token: String, val amount: Int) : PaymentStateEvent()
    class None() : PaymentStateEvent()
}