package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("token_type") val type: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("expires_at") val time: String?
)