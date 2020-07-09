package com.voodoolab.eco.interfaces

import com.voodoolab.eco.ui.DataState

interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)

}