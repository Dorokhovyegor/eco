package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.LevelsCashBackModel
import com.voodoolab.eco.models.UserInfo

data class UserInfoResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: UserInfo?,
    @SerializedName("month-cash-back") val month_cash_back: LevelsCashBackModel?

)