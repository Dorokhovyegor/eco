package com.voodoolab.eco.models


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("operation_id")
    val operationId: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("user_id")
    val userId: Int?
)