package com.voodoolab.eco.network

import androidx.lifecycle.LiveData
import com.voodoolab.eco.models.CityModel
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

    @Multipart
    @POST("api/user/set-name")
    fun setNewName(
        @Header("Authorization") token: String,
        @Part("name") name: String
    ): LiveData<GenericApiResponse<UpdateNameResponse>>

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
    ): LiveData<GenericApiResponse<ObjectResponse>>

    @GET("api/cities")
    fun getAllCities(
        @Header("Accept") accept: String
    ): LiveData<GenericApiResponse<CitiesResponse>>

    @Multipart
    @POST("api/user/set-city")
    fun setCity(
        @Header("Authorization") tokenApp: String,
        @Part("city") city: String?
    ): LiveData<GenericApiResponse<UpdateCityResponse>>

    @Multipart
    @POST("api/user/set-firebase-token")
    fun sendTokenToServer(
        @Header("Authorization") tokenApp: String,
        @Part("token") tokenFireBase: String
    ): LiveData<GenericApiResponse<UpdateTokenResponse>>

    @GET("api/stock")
    fun getSpecialOffers(
        @Header("Authorization") tokenApp: String,
        @Query("city") city: String?,
        @Query("page") page: String,
        @Query("qty") qty: String
    ): Call<SpecialOffersResponse>

    @GET("api/stock/{id}")
    fun getSpecialOfferById(
        @Header("Authorization") tokenApp: String,
        @Path("id") discountId: Int
    ): LiveData<GenericApiResponse<DiscountResponse>>

    @GET("api/user/logout")
    fun logout(
        @Header("Authorization") tokenApp: String
    ): LiveData<GenericApiResponse<LogoutResponse>>

}