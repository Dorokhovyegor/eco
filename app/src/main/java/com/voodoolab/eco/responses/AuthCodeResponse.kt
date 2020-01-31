package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class AuthCodeResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("msg") val msg: String?,
    @SerializedName("seconds_to_send") val time: Int?)