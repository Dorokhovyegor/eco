package com.voodoolab.eco.responses
import com.google.gson.annotations.SerializedName

data class ListObjectResponse(
   @SerializedName("washes") val list: List<ObjectResponse>?
)