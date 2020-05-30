package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class UrlResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: PaymentData
)

data class PaymentData(
    @SerializedName("url") val url: String
)