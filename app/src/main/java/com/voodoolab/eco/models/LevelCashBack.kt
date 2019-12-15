package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class LevelCashBack(
    @SerializedName("percent") val percent: Int?,
    @SerializedName("value") val value: Int?
)