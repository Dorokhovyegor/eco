package com.voodoolab.eco.broadcast_receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.voodoolab.eco.R
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.ui.MainActivity
import com.voodoolab.eco.utils.Constants

class ReportBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val operationId = intent?.getIntExtra(Constants.NOTIFICATION_OPERATION_ID, -1)
        val type = intent?.getStringExtra(Constants.NOTIFICATION_TYPE)
        val washModel = intent?.getStringExtra(Constants.NOTIFICATION_WASH_MODEL)
        val channel = intent?.getStringExtra(Constants.NOTIFICATION_CHANNEL)
        val title = intent?.getStringExtra(Constants.NOTIFICATION_TITLE)
        val value = intent?.getIntExtra(Constants.NOTIFICATION_VALUE_OF_TRANSACTION, -1)

        context?.let {
            if (channel != null && washModel != null && type != null) {
                val toMainActivityIntent = Intent(Constants.REPORT_NOTIFICATION_DETECTED)
                toMainActivityIntent.putExtra(Constants.NOTIFICATION_OPERATION_ID, operationId)
                toMainActivityIntent.putExtra(Constants.NOTIFICATION_WASH_MODEL, washModel)
                toMainActivityIntent.putExtra(Constants.NOTIFICATION_VALUE_OF_TRANSACTION, value)
                toMainActivityIntent.putExtra(Constants.NOTIFICATION_TITLE, title)
                context.sendBroadcast(toMainActivityIntent)
            }
        }
    }
}