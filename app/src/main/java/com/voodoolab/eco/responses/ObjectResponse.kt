package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName

data class ObjectResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("city") val city: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("coordinated") val coordinates: String?,
    @SerializedName("cashback") val cashback: Int?,
    @SerializedName("seats") val seats: Int?
)