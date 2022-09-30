package com.app.inails.booking.admin.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.views.main.MainActivity
import java.util.concurrent.atomic.AtomicInteger


class NotificationsManager(private val mContext: Context) {

    val id: Int
        get() = 5

    fun defaultNotify(cloudMessage: FireBaseCloudMessage) {
        val pendingIntent = MainActivity.getPendingIntent(mContext, cloudMessage)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(mContext, CHANNEL_ID_DEFAULT)
            .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            .setTicker(mContext.getString(R.string.app_name))
            .setAutoCancel(true)
            .setContentTitle(cloudMessage.data.salon_name)
            .setContentText(cloudMessage.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLights(Color.MAGENTA, 3000, 3000)
            .setVibrate(longArrayOf(500, 500))
            .setSound(defaultSoundUri)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cloudMessage.body))

        if (pendingIntent != null) notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                mContext.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true)
            channel.lightColor = Color.CYAN
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            id,
            notificationBuilder.build()
        )
    }

    /**
     * @param id~ Cancel notification
     */
    fun cancelNotification(id: Int) {
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    fun cancelAll() {
        getManager().cancelAll()
    }

    companion object {
        private val c = AtomicInteger(0)
        private const val CHANNEL_ID_DEFAULT = "channel_default"
    }

    private fun getManager(): NotificationManager {
        return mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}
