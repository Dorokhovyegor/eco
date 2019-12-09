package com.voodoolab.eco.states.code_state

sealed class CodeStateEvent {

    class RequestCodeEvent(
        val phoneNumber: String?
    ): CodeStateEvent()

    class None: CodeStateEvent()

}