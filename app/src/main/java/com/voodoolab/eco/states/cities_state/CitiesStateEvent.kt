package com.voodoolab.eco.states.cities_state

sealed class CitiesStateEvent {

    class RequestCityList: CitiesStateEvent()

    class UpdateCity(val token: String, val city: String): CitiesStateEvent()

    class None: CitiesStateEvent()

}