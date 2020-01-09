package com.voodoolab.eco.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.widget.AutoCompleteTextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.voodoolab.eco.R
import com.voodoolab.eco.utils.Constants


val REPORT_NOTIFICATION = "review"
val SPECIAL_OFFER_NOTIFICATION = "stock"
val OTHER_NOTIFICATION = "free"

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {

        p0.data.let { dataMessage ->

            val type = dataMessage["type"]
            println("DEBUG: ${type}")
            val title = p0.notification?.title

            when (type) {
                REPORT_NOTIFICATION -> {
                    var not = NotificationCompat.Builder(applicationContext, Constants.CHANNEL_REPORT)
                        .setSmallIcon(R.drawable.discount_icon)
                        .setContentTitle(title)
                        .setContentText("Оставьте отзыв, чтобы мы узнали, как сделать наш сервис лучше")
                        .build()

                    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(1, not)
                    println("DEBUG i am here")
                }
                SPECIAL_OFFER_NOTIFICATION -> {

                }
                OTHER_NOTIFICATION -> {

                } else -> {}
            }
        }

        //todo type - тип сообщения, может быть stock, free и еще для отзыва
        // todo alert_id - хз
        // stock_id -- акция, которую надо открыть

        p0.data.isNotEmpty().let {
            println("DEBUG: data: ${p0.data}")
        }


    }
}