package com.voodoolab.eco.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)
    fun expandAppBar()
    fun hideSoftKeyBoard()
}