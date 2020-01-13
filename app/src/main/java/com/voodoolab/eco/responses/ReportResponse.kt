package com.voodoolab.eco.responses


import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.Data

data class ReportResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String?
)