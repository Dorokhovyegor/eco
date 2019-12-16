package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class WashModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("city") val city: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("coordinates") val coordinates: String?,
    @SerializedName("cashback") val cashback: Int?,
    @SerializedName("seats") val seats: Int?
)