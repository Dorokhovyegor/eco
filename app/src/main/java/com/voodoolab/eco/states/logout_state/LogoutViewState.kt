package com.voodoolab.eco.states.logout_state

import com.voodoolab.eco.responses.LogoutResponse

data class LogoutViewState(
    var logoutResponse: LogoutResponse? = null
)