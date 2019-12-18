package com.voodoolab.eco.network

import androidx.lifecycle.LiveData
import com.voodoolab.eco.responses.*
import com.voodoolab.eco.utils.GenericApiResponse
import retrofit2.Call
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

    @GET("api/user")
    fun getUserInfo(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): LiveData<GenericApiResponse<UserInfoResponse>>

    @GET("api/user/operations")
    fun getOperations(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("page") page: String,
        @Query("qty") qty: String
    ): Call<TransactionResponse>

    @GET("api/wash")
    fun getAllObjects(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): LiveData<GenericApiResponse<ListObjectResponse>>

    @GET("api/wash/{id}")
    fun getObjectById(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("id") washId: Int
    ) : LiveData<GenericApiResponse<ObjectResponse>>
}