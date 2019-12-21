package com.voodoolab.eco.responses

import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.CityModel

data class CitiesResponse(
    @SerializedName("cities") val listCities: List<CityModel>?
)