package com.voodoolab.eco.network

import androidx.lifecycle.LiveData
import com.voodoolab.eco.responses.AuthCodeResponse
import com.voodoolab.eco.utils.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.GET

interface ApiService {

    @GET("api/get-auth-code")
    fun requestCode(
        @Field("phone") phone: String
    ): LiveData<GenericApiResponse<AuthCodeResponse>>
}