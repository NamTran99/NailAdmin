package com.app.inails.booking.admin.service

import android.annotation.SuppressLint
import android.support.core.livedata.post
import android.support.di.inject
import android.util.Log
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.notification.NotificationsManager
import com.app.inails.booking.admin.views.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject

class MyFirebaseMessageService : FirebaseMessagingService() {

    private val TAG: String = "MyFirebaseMessageService"
    val appEvent by inject<AppEvent>()
    private lateinit var appCache: AppCache

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.d(TAG, "onMessageReceived: NamTD8")
            val params = remoteMessage.data
            val `object` = JSONObject(params as Map<*, *>?)
            val objectFilter =
                `object`.toString().replace("\"{", "{").replace("}\"", "}").replace("\\\"", "\"")
            val cloudMessage =
                Gson().fromJson(objectFilter, FireBaseCloudMessage::class.java)
            pushNotificationDefault(cloudMessage as FireBaseCloudMessage, MainActivity::class.java)
            appEvent.notifyCloudMessage.post(cloudMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun pushNotificationDefault(cloudMessage: FireBaseCloudMessage, classIntent: Class<*>) {
        NotificationsManager(applicationContext).defaultNotify(cloudMessage, classIntent)
    }

    @SuppressLint("LongLogTag")
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        appCache = AppCache(applicationContext)
        appCache.deviceToken = p0
        Log.d(TAG, "NamTD8 onTokenRefresh: ${appCache.deviceToken} ")
    }
}