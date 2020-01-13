package com.voodoolab.eco.models


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("review")
    val review: Review
)