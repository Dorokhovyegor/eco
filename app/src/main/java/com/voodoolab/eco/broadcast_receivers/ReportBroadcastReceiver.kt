package com.voodoolab.eco.broadcast_receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_NO_CREATE
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.voodoolab.eco.R
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.utils.Constants

class ReportBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val operationId = intent?.getIntExtra(Constants.NOTIFICATION_OPERATION_ID, -1)
        val type = intent?.getStringExtra(Constants.NOTIFICATION_TYPE)
        val washModel = intent?.getParcelableExtra<WashModel>(Constants.NOTIFICATION_WASH_MODEL)
        val channel = intent?.getStringExtra(Constants.NOTIFICATION_CHANNEL)
        val title = intent?.getStringExtra(Constants.NOTIFICATION_TITLE)
        val value = intent?.getIntExtra(Constants.NOTIFICATION_VALUE_OF_TRANSACTION, -1)

        context?.let {
            if (channel != null && washModel != null && type != null) {
                val not =
                    NotificationCompat.Builder(context, Constants.CHANNEL_REPORT)
                        .setSmallIcon(R.drawable.discount_icon)
                        .setContentTitle(title)
                        .setContentText("Написать отзыв ${washModel.address}")
                        .addAction(R.drawable.map_icon, "Написать отзыв", PendingIntent.getActivity(context, 1000, Intent(
                            context, MainActivity::class.java
                        ).apply {
                            putExtra(Constants.NOTIFICATION_OPERATION_ID, operationId)
                            putExtra(Constants.NOTIFICATION_WASH_MODEL, washModel)
                            putExtra(Constants.NOTIFICATION_VALUE_OF_TRANSACTION, value)
                        }, 0))
                        .setAutoCancel(true)
                        .build()

                val nm = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(1, not)


            }
        }
    }
}