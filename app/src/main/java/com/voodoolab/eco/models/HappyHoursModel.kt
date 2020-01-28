package com.voodoolab.eco.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HappyHoursModel(
    @SerializedName("active") val active: Boolean?,
    @SerializedName("switch") val switch: Boolean?,
    @SerializedName("start") val start: String?,
    @SerializedName("end") val end: String?
): Parcelable