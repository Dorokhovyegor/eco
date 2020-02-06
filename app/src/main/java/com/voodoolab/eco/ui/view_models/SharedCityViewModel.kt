package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedCityViewModel : ViewModel() {

    private var cityLiveData: MutableLiveData<String?> = MutableLiveData()
    private var coord: MutableLiveData<ArrayList<Double>> = MutableLiveData()

    fun setCity(city: String?) {
        cityLiveData.value = city
    }

    fun getCity(): LiveData<String?> {
        return cityLiveData
    }

    fun setCoord(coordinates: ArrayList<Double>) {
        println("MapFragment: ${coordinates}")
        coord.value = coordinates
    }

    fun getCoord(): LiveData<ArrayList<Double>> {
        return coord
    }
}