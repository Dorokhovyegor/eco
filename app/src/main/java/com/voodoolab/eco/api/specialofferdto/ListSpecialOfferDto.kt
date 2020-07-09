package com.voodoolab.eco.api.specialofferdto

import com.google.gson.annotations.SerializedName
import com.voodoolab.eco.models.SpecialOfferModel

data class ListSpecialOfferDto(
    @SerializedName("current_page") val page: Int,
    @SerializedName("data") val offers: List<SpecialOfferModel>?,
    @SerializedName("first_page_url") val firstPageUrl: String?,
    @SerializedName("from") val from: Int,
    @SerializedName("last_page") val lastPage: Int?,
    @SerializedName("last_page_url") val lastPageUrl: String?,
    @SerializedName("next_page_url") val nextPageUrl: String?,
    @SerializedName("path") val path: String?,
    @SerializedName("per_page") val perPage: Int?,
    @SerializedName("prev_page_url") val prevPageUrl: String?,
    @SerializedName("to") val to: Int?,
    @SerializedName("total") val total: Int?
)