package com.voodoolab.eco.states.user_state

sealed class UserStateEvent {

    class RequestUserInfo(
        val token: String
    ): UserStateEvent()

    class None: UserStateEvent()
}