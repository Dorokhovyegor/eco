package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class LevelsCashBackModel(
    @SerializedName("1") val levelCashBack1: LevelCashBack?,
    @SerializedName("2") val levelCashBack2: LevelCashBack?,
    @SerializedName("3") val levelCashBack3: LevelCashBack?,
    @SerializedName("4") val levelCashBack4: LevelCashBack?,
    @SerializedName("5") val levelCashBack5: LevelCashBack?
)