package com.voodoolab.eco.responses


import com.google.gson.annotations.SerializedName

data class Errors(
    @SerializedName("name")
    val name: List<String>?
)