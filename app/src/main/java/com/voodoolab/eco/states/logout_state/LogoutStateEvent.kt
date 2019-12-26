package com.voodoolab.eco.states.logout_state

sealed class LogoutStateEvent {

    class LogoutEvent(
        val token: String
    ): LogoutStateEvent()
}