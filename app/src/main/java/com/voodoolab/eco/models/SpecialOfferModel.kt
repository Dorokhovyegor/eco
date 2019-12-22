package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class SpecialOfferModel(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: String?,
    @SerializedName("started_at") val startTime: String?,
    @SerializedName("finished_at") val endTime: String?,
    @SerializedName("cashback") val cashBack: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("text") val body: String?,
    @SerializedName("logo") val imageUrl: String?
)