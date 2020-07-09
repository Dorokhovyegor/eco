package com.voodoolab.eco.network

import com.voodoolab.eco.utils.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.voodoolab.eco.api.ApiService


object RetrofitBuilder {

    const val BASE_URL = "http://loyalty.myeco24.ru/"

    val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val httpClient = OkHttpClient.Builder().addInterceptor(logging)

    val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }


    val checkerConnection: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl("http://188.225.78.188/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }

    val apiService: ApiService by lazy {
        retrofitBuilder
            .build()
            .create(ApiService::class.java)
    }

    val connectionInterceptor: ApiService by lazy {
        checkerConnection
            .build()
            .create(ApiService::class.java)
    }
}
