package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.TokenData

data class LoginResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val responseData: TokenData?
)