package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("balance") val balance: Int?,
    @SerializedName("city") val city: String?,
    @SerializedName("month_spent") val month_spent: Int?,
    @SerializedName("mutable_city") val mutable_city: Boolean?
)