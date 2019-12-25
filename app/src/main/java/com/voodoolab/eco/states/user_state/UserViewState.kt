package com.voodoolab.eco.states.user_state

import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.responses.UserInfoResponse

data class UserViewState(
    var userResponse: UserInfoResponse? = null,
    var clearResponse: ClearUserModel? = null
)