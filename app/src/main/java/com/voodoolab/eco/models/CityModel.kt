package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class CityModel(
    @SerializedName("city") val city: String,
    @SerializedName("coordinates") val coordinates: List<Double>
)