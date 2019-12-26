package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class UpdateNameResponse(
    @SerializedName("status") val status: String?
)