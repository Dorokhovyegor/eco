package com.voodoolab.eco.states.firebase_token_state

sealed class UpdateTokenFireBaseStateEvent {

    class UpdateTokenEvent(
        val appToken: String,
        val firebaseToken: String
    ): UpdateTokenFireBaseStateEvent()
}