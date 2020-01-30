package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.LevelCashBack
import com.voodoolab.eco.models.LevelsCashBackModel
import com.voodoolab.eco.models.UserInfo

data class UserInfoResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: UserInfo?,
    @SerializedName("month_cash_back") val month_cash_back: List<LevelCashBack>?
)
