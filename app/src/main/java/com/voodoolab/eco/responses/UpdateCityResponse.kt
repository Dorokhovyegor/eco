package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class UpdateCityResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("errors") val errors: Errors?,
    @SerializedName("message") val message: String?
)