package com.voodoolab.eco.models

data class ClearUserModel(
    var balance: Int?,
    val name: String?,
    val valuesMoney: ArrayList<Int>?,
    val valuesPercent: ArrayList<Int>?,
    val indicatorPosition: Int?,
    val currentProgressInPercent: Float?
)