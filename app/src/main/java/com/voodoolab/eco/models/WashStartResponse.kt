package com.voodoolab.eco.models


import com.google.gson.annotations.SerializedName

data class WashStartResponse(
    @SerializedName("msg")
    val  msg: String?,
    @SerializedName("status")
    val status: String?
)