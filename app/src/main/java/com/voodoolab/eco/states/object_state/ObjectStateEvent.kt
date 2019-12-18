package com.voodoolab.eco.states.object_state

sealed class ObjectStateEvent {

    class RequestObjectEvent(
        val token: String,
        val id: Int
    ): ObjectStateEvent()

    class None: ObjectStateEvent()

}