package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("balance") val balance: Int?,
    @SerializedName("month_balance") val month_balance: Int?
)