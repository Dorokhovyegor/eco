package com.voodoolab.eco.states.user_state

sealed class UserStateEvent {
    class RequestUserInfo(
        val token: String
    ): UserStateEvent()

    class SetNewNameEvent(
        val token: String,
        val name: String
    ): UserStateEvent()

    class SetCityEvent(
        val token: String,
        val cityName: String
    ): UserStateEvent()

    class None: UserStateEvent()
}