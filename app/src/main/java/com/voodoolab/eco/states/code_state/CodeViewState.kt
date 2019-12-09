package com.voodoolab.eco.states.code_state

import com.voodoolab.eco.responses.AuthCodeResponse

data class CodeViewState(
    var codeResponse: AuthCodeResponse? = null
)