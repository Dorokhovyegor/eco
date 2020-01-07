package com.voodoolab.eco.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClearUserModel(
    var balance: Int?,
    val name: String?,
    val valuesMoney: ArrayList<Int>?,
    val valuesPercent: ArrayList<Int>?,
    val indicatorPosition: Int?,
    val currentProgressInPercent: Float?,
    val nextLevelOfCashBack: Int?
): Parcelable