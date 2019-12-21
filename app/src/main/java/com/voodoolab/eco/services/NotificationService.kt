package com.voodoolab.eco.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {

        println("DEBUG: FROM ${p0.from}")


        //todo type - тип сообщения, может быть stock, free и еще для отзыва
        // todo alert_id - хз
        // stock_id -- акция, которую надо открыть

        p0.data.isNotEmpty().let {
            println("DEBUG: data: ${p0.data}")
        }

        p0.notification?.let {
            println("DEBUG: body ${it.body}")
        }
    }
}