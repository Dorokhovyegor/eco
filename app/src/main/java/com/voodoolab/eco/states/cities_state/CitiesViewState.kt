package com.voodoolab.eco.states.cities_state

import com.voodoolab.eco.models.CityModel

data class CitiesViewState(
    var citiesResponse: List<CityModel>? = null
)