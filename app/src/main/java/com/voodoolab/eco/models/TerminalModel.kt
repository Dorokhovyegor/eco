package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class TerminalModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?
)