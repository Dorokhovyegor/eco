package com.voodoolab.eco.api

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.voodoolab.eco.api.specialofferdto.SpecialOfferDto
import com.voodoolab.eco.api.specialofferdto.ListSpecialOfferDto
import com.voodoolab.eco.models.SetDiscountViewedResponse
import com.voodoolab.eco.models.WashStartResponse
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
        @Query("qty") qty: String,
        @Query("period_from") from: String?,
        @Query("period_to") to: String?,
        @Query("types[]") types: ArrayList<String>?
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
    @POST("api/v1/users/set-city")
    fun setCity(
        @Header("Authorization") tokenApp: String,
        @Part("city") city: String?
    ): LiveData<GenericApiResponse<UserInfoResponse>>

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
        @Query("page") page: Int,
        @Query("qty") qty: Int
    ): LiveData<GenericApiResponse<ListSpecialOfferDto>>

    @GET("api/stock/{id}")
    fun getSpecialOfferById(
        @Header("Authorization") tokenApp: String,
        @Path("id") discountId: Int
    ): LiveData<GenericApiResponse<SpecialOfferDto>>

    @GET("api/user/logout")
    fun logout(
        @Header("Authorization") tokenApp: String
    ): LiveData<GenericApiResponse<LogoutResponse>>

    @Multipart
    @POST("api/reviews")
    fun sentReport(
        @Header("Authorization") tokenApp: String,
        @Part("operation_id") operationId: Int?,
        @Part("text") textReport: String? = "...",
        @Part("stars") rating: Double?
    ): LiveData<GenericApiResponse<ReportResponse>>

    @Multipart
    @POST("api/reviews")
    fun sentReport(
        @Header("Authorization") tokenApp: String,
        @Part("operation_id") operationId: Int?,
        @Part("text") textReport: String? = "...",
        @Part("stars") rating: Int?
    ): LiveData<GenericApiResponse<ReportResponse>>

    @Multipart
    @POST("user/set-viewed-stock")
    fun setViewedDiscount(
        @Header("Authorization") tokenApp: String,
        @Part("stock_id") id: Int
    ): LiveData<GenericApiResponse<SetDiscountViewedResponse>>

    @Multipart
    @POST("api/user/replenish")
    fun getHtmlFromServer(
        @Header("Authorization") tokenApp: String,
        @Part("amount") id: Int,
        @Part("url") enableMode: Boolean = true
    ): LiveData<GenericApiResponse<UrlResponse>>

    @GET("/api/check")
    fun requestConnection(): Call<JsonObject>

    @Multipart
    @POST("api/start-wash")
    fun startWash(
        @Header("Authorization") tokenApp: String,
        @Part("code") washCode: String
    ): LiveData<GenericApiResponse<WashStartResponse>>
}