package com.voodoolab.eco.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.widget.AutoCompleteTextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.*
import com.orhanobut.hawk.Parser
import com.voodoolab.eco.R
import com.voodoolab.eco.broadcast_receivers.ReportBroadcastReceiver
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.Constants.NOTIFICATION_CHANNEL
import com.voodoolab.eco.utils.Constants.NOTIFICATION_OPERATION_ID
import com.voodoolab.eco.utils.Constants.NOTIFICATION_TITLE
import com.voodoolab.eco.utils.Constants.NOTIFICATION_TYPE
import com.voodoolab.eco.utils.Constants.NOTIFICATION_VALUE_OF_TRANSACTION
import com.voodoolab.eco.utils.Constants.NOTIFICATION_WASH_MODEL
import com.voodoolab.eco.utils.Json
import java.text.ParseException


val REPORT_NOTIFICATION = "review"
val SPECIAL_OFFER_NOTIFICATION = "stock"
val OTHER_NOTIFICATION = "free"

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        p0.data.let { dataMessage ->
            try {
                val title = p0.notification?.title
                val operationId = dataMessage[NOTIFICATION_OPERATION_ID]?.toInt()
                val type = dataMessage[NOTIFICATION_TYPE]
                val washString = dataMessage[NOTIFICATION_WASH_MODEL]
                val value = dataMessage[NOTIFICATION_VALUE_OF_TRANSACTION]?.toInt()

                // защитить это от падения
                val washJson = JsonParser().parse(washString) as JsonObject
                // get data for washModel
                val washAddress = washJson.get("address").asString
                val washCity = washJson.get("city").asString
                val washCashBackValue = washJson.get("cashback").asInt
                val washId = washJson.get("id").asInt
                val seats = washJson.get("seats").asInt

                val washModel = WashModel(
                    id = washId,
                    city = washCity,
                    address = washAddress,
                    coordinates = null,
                    cashback = washCashBackValue,
                    seats = seats
                )
                when (type) {
                    REPORT_NOTIFICATION -> {
                        sendBroadcast(
                            Intent(this, ReportBroadcastReceiver::class.java).apply {
                                putExtra(NOTIFICATION_TITLE, title)
                                putExtra(NOTIFICATION_OPERATION_ID, operationId)
                                putExtra(NOTIFICATION_VALUE_OF_TRANSACTION, value)
                                putExtra(NOTIFICATION_TYPE, type)
                                putExtra(NOTIFICATION_WASH_MODEL, washModel)
                                putExtra(NOTIFICATION_CHANNEL, REPORT_NOTIFICATION)
                            }
                        )
                    }
                    SPECIAL_OFFER_NOTIFICATION -> {

                    }
                    OTHER_NOTIFICATION -> {

                    }
                    else -> {
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                return
            } catch (jsonException: JsonParseException) {
                jsonException.printStackTrace()
                return
            } catch (jsonIOException: JsonIOException) {
                jsonIOException.printStackTrace()
                return
            } catch (npe: NullPointerException) {
                npe.printStackTrace()
                return
            }
        }
    }
}