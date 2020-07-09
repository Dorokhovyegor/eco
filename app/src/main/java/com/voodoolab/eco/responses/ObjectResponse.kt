package com.voodoolab.eco.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.HappyHoursModel
import com.voodoolab.eco.models.SpecialOfferModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ObjectResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("city") val city: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("coordinates") val coordinates: ArrayList<Double>?,
    @SerializedName("cashback") val cashback: Int?,
    @SerializedName("seats") val seats: Int?,
    @SerializedName("system_id") val systemId: String,
    @SerializedName("happy-hours") val happyHoursInfo: HappyHoursModel?,
    @SerializedName("stocks") val stocks: ArrayList<SpecialOfferModel>?
): Parcelable