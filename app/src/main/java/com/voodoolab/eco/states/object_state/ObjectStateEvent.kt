package com.voodoolab.eco.states.object_state

sealed class ObjectStateEvent {

    class RequestObjectEvent(
        val id: Int
    ): ObjectStateEvent()

    class None: ObjectStateEvent()

}