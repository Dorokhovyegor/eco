package com.voodoolab.eco.models


import com.google.gson.annotations.SerializedName

data class Pivot(
    @SerializedName("stock_id")
    val stockId: Int,
    @SerializedName("wash_id")
    val washId: Int
)