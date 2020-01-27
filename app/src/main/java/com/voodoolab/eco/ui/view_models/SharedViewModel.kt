package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private var params: MutableLiveData<Map<String, Any?>?> = MutableLiveData()

    fun setParams(map: Map<String, Any?>?) {
        println("DEBUG: внутри set params")
        params.value = map
    }

    fun getParams(): LiveData<Map<String, Any?>?> {
        println("DEBUG: внутри get params")
        return params
    }
}