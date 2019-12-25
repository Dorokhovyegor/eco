package com.voodoolab.eco.states.cities_state

import com.voodoolab.eco.responses.CitiesResponse
import com.voodoolab.eco.responses.UpdateCityResponse

data class CitiesViewState(
    var citiesResponse: CitiesResponse? = null,
    var updateCityResponse: UpdateCityResponse? = null
)