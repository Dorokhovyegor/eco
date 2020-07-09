package com.voodoolab.eco.api.specialofferdto


import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.WashModel

data class SpecialOfferDto(
    @SerializedName("cashback")
    val cashback: Int?,
    @SerializedName("finished_at")
    val finishedAt: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("started_at")
    val startedAt: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("washes")
    val washes: List<WashModel>
)