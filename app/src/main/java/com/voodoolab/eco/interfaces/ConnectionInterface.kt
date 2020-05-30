package com.voodoolab.eco.interfaces

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ConnectionInterface<T> : Callback<T> {
    fun onConnect(connect: Call<T>, responseConnection: Response<T>)
    fun onDisconnect(connect: Call<T>, t: Throwable)
}