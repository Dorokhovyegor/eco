package com.voodoolab.eco.utils

import com.google.android.gms.wallet.WalletConstants

object Constants {
    /**
     * Changing this to ENVIRONMENT_PRODUCTION will make the API return chargeable card information.
     * Please refer to the documentation to read about the required steps needed to enable
     * ENVIRONMENT_PRODUCTION.
     *
     * @value #PAYMENTS_ENVIRONMENT
     */
    const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

    /**
     * The allowed networks to be requested from the API. If the user has cards from networks not
     * specified here in their account, these will not be offered for them to choose in the popup.
     *
     * @value #SUPPORTED_NETWORKS
     */
    val SUPPORTED_NETWORKS = listOf(
        "AMEX",
        "DISCOVER",
        "JCB",
        "MASTERCARD",
        "VISA")

    /**
     * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
     * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
     *
     * @value #SUPPORTED_METHODS
     */
    val SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS")

    /**
     * Required by the API, but not visible to the user.
     *
     * @value #COUNTRY_CODE Your local country
     */
    const val COUNTRY_CODE = "RU"

    /**
     * Required by the API, but not visible to the user.
     *
     * @value #CURRENCY_CODE Your local currency
     */
    const val CURRENCY_CODE = "RUB"
    /**
     * The name of your payment processor/gateway. Please refer to their documentation for more
     * information.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_NAME
     */
    const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "example"

    /**
     * Custom parameters required by the processor/gateway.
     * In many cases, your processor / gateway will only require a gatewayMerchantId.
     * Please refer to your processor's documentation for more information. The number of parameters
     * required and their names vary depending on the processor.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS
     */
    val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
        "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
        "gatewayMerchantId" to "exampleGatewayMerchantId"
    )

    /**
     * Only used for `DIRECT` tokenization. Can be removed when using `PAYMENT_GATEWAY`
     * tokenization.
     *
     * @value #DIRECT_TOKENIZATION_PUBLIC_KEY
     */
    const val DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME"

    /**
     * Parameters required for `DIRECT` tokenization.
     * Only used for `DIRECT` tokenization. Can be removed when using `PAYMENT_GATEWAY`
     * tokenization.
     *
     * @value #DIRECT_TOKENIZATION_PARAMETERS
     */
    val DIRECT_TOKENIZATION_PARAMETERS = mapOf(
        "protocolVersion" to "ECv1",
        "publicKey" to DIRECT_TOKENIZATION_PUBLIC_KEY
    )

    val TOKEN = "token"
    val TYPE_TOKEN = "token_type"

    val SETTINGS = "eco_settings"
    val CITY_ECO = "city"
    val CITY_COORDINATES = "coordinates"

    val FILTER_SETTING = "eco_filter"

    val FILTER_WASTE = "waste"
    val FILTER_CASHBACK = "cashback"
    val FILTER_MONTHBONUS = "month_bonus"
    val FILTER_REPLENISH_OFFLINE = "replenish_offline"
    val FILTER_REPLENISH_ONLINE = "replenish_online"
    var FILTER_ALL_TIME = "all_time"

    val FILTER_PERIOD_FROM = "period_from"
    val FILTER_PERIOD_TO = "period_to"

    val CHANNEL_REPORT = "CHANNEL_REPORT"
    val CHANNEL_SPECIAL_OFFER = "CHANNEL_SPECIAL_OFFER"
    val CHANNEL_FREE = "CHANNEL_FREE"

    // todo add constant for intents

    val NOTIFICATION_TITLE = "title"
    val NOTIFICATION_OPERATION_ID = "operation_id"
    val NOTIFICATION_VALUE_OF_TRANSACTION = "value"
    val NOTIFICATION_TYPE = "type"
    val NOTIFICATION_WASH_MODEL = "wash"
    val NOTIFICATION_CHANNEL = "channel"
    val NOTIFICATION_WASH_ADDRESS = "address"
    val NOTIFICATION_WASH_CITY = "city"

    val REPORT_NOTIFICATION_DETECTED = "com.voodoolab.eco.action.NEW_REPORT_ACTION"

    val TRANSACTION_HEADER_TYPE = "header"
    val TRANSACTION_FOOTER_TYPE = "footer"
    val TRANSACTION_TYPE = "transaction"




    // params for request
    val REQUEST_PARAM_WASTE ="waste"
    val REQUEST_PARAM_CASHBACK ="cashback"
    val REQUEST_PARAM_MONTHBONUS ="month_bonus"
    val REQUEST_PARAM_REPLENISH_OFFLINE ="replenish_offline"
    val REQUEST_PARAM_REPLENISH_ONLINE ="replenish_online"

    val REQUEST_PARAM_PERIOD_FROM ="period_from"
    val REQUEST_PARAM_PERIOD_TO ="period_to"

    //




}