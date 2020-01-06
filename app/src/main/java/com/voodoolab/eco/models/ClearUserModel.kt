package com.voodoolab.eco.models

import android.os.Parcel
import android.os.Parcelable

data class ClearUserModel(
    var balance: Int?,
    val name: String?,
    val valuesMoney: ArrayList<Int>?,
    val valuesPercent: ArrayList<Int>?,
    val indicatorPosition: Int?,
    val currentProgressInPercent: Float?
): Parcelable {
}