package com.voodoolab.eco.states.auth_state

sealed class AuthStateEvent {

    class LoginEvent(
        val phoneNumber: String?,
        val code: String?
    ): AuthStateEvent()

    class None: AuthStateEvent()

}