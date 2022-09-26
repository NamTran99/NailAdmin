package com.app.inails.booking.admin.service

import android.annotation.SuppressLint
import android.support.core.livedata.post
import android.support.di.inject
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
    private val appCache by inject<AppCache>()

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            val params = remoteMessage.data
            val `object` = JSONObject(params as Map<*, *>?)
            val objectFilter =
                `object`.toString().replace("\"{", "{").replace("}\"", "}").replace("\\\"", "\"")
            val cloudMessage =
                Gson().fromJson(objectFilter, FireBaseCloudMessage::class.java)
            NotificationsManager(applicationContext).defaultNotify(cloudMessage)
            appEvent.notifyCloudMessage.post(cloudMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("LongLogTag")
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        appCache.deviceToken = p0
    }
}