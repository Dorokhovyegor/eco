package com.voodoolab.eco.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClearUserModel(
    val balance_rub:  Int?,
    val name: String?,
    val city: String?,
    val valuesMoney: ArrayList<Int>?,
    val valuesPercent: ArrayList<Int>?,
    val indicatorPosition: Int?,
    val currentProgressInPercent: Float?,
    val nextLevelOfCashBack: Int?
): Parcelable