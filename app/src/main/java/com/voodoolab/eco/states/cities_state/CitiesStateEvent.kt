package com.voodoolab.eco.states.cities_state

sealed class CitiesStateEvent {
    class RequestCityList: CitiesStateEvent()

    class None: CitiesStateEvent()
}