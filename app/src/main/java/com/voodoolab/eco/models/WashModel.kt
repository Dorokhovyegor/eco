package com.voodoolab.eco.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WashModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("city") val city: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("coordinates") val coordinates: ArrayList<Double>?,
    @SerializedName("cashback") val cashback: Int?,
    @SerializedName("system_id") val systemId: String?,
    @SerializedName("seats") val seats: Int?
): Parcelable