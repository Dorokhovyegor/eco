package com.voodoolab.eco.states.object_state

sealed class ListObjectStateEvent {

    class RequestAllObjectEvent(
        val token: String
    ): ListObjectStateEvent()

    class None: ListObjectStateEvent()
}