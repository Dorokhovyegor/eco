package com.voodoolab.eco.network

import androidx.lifecycle.LiveData
import com.voodoolab.eco.responses.AuthCodeResponse
import com.voodoolab.eco.responses.LoginResponse
import com.voodoolab.eco.utils.GenericApiResponse
import retrofit2.http.*

interface ApiService {

    @GET("api/get-auth-code")
    fun requestCode(
        @Query("phone") phone: String
    ): LiveData<GenericApiResponse<AuthCodeResponse>>

    @Multipart
    @POST("api/login")
    fun login(
        @Part("phone") phone: String,
        @Part("code") code: String
    ): LiveData<GenericApiResponse<LoginResponse>>
}