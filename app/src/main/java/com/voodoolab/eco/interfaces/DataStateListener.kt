package com.voodoolab.eco.interfaces

import com.voodoolab.eco.network.DataState

interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)

}