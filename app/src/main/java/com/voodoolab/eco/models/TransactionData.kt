package com.voodoolab.eco.models

import com.google.gson.annotations.SerializedName

data class TransactionData(
    @SerializedName("id") val id: Int?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("type") val type: Int?,
    @SerializedName("value") val value: Int?,
    @SerializedName("wash_id") val washId: Int?,
    @SerializedName("terminal_id") val terminalId: Int?,
    @SerializedName("created_at") val createdAt: Int?,
    @SerializedName("wash") val wash: WashModel?,
    @SerializedName("terminal") val terminal: TerminalModel?
)