package com.voodoolab.eco.states.discount_state

import com.voodoolab.eco.models.SpecialOfferModel
import com.voodoolab.eco.models.WashModel

data class SpecialOfferViewState(
    var specialOfferFields: SpecialOfferFields = SpecialOfferFields(),
    var specialOfferList: SpecialOfferListFields = SpecialOfferListFields()
)

data class SpecialOfferFields(
    var title: String? = null,
    var body: String? = null,
    var imageUrl: String? = null,
    var washList: List<WashModel>? = ArrayList()
)

data class SpecialOfferListFields(
    var list: List<SpecialOfferModel> = ArrayList(),
    var page: Int = 1,
    var isQueryInProgress: Boolean = false,
    var isQueryExhausted: Boolean = false
)