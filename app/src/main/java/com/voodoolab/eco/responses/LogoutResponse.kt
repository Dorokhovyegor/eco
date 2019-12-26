package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("status") val status: String?
)