package com.voodoolab.eco.states.startwash_state

sealed class StartWashStateEvent {

    class StartWashViaCode(
        val token: String,
        val code: String): StartWashStateEvent()

    class None() : StartWashStateEvent()

}