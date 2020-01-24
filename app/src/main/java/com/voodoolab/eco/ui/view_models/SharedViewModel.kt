package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private var params: MutableLiveData<Map<String, Any?>> = MutableLiveData()

    fun setParams(map: Map<String, Any?>) {
        params.value = map
    }

    fun getParams(): LiveData<Map<String, Any?>> {
        return params
    }
}