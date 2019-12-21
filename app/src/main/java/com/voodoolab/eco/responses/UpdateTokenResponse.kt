package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class UpdateTokenResponse(
    @SerializedName("status") val status: String?
)