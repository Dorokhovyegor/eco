package com.voodoolab.eco.states.auth_state

import com.voodoolab.eco.responses.LoginResponse

data class AuthViewState(
    var loginResponse: LoginResponse? = null
)