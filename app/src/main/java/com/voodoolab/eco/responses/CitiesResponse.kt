package com.voodoolab.eco.responses

import com.voodoolab.eco.models.CityModel

data class CitiesResponse(
    val listCities: List<CityModel>?
)