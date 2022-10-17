package com.app.inails.booking.admin.service

import android.support.core.livedata.post
import android.support.di.inject
import android.util.Log
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.notification.NotificationsManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject

class MyFirebaseMessageService : FirebaseMessagingService() {

    val appEvent by inject<AppEvent>()
    private val appCache by inject<AppCache>()
    private val userLocalSource by inject<UserLocalSource>()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {

            val params = remoteMessage.data
            val `object` = JSONObject(params as Map<*, *>)
            val objectFilter =
                `object`.toString().replace("\"{", "{").replace("}\"", "}").replace("\\\"", "\"")
            val cloudMessage =
                Gson().fromJson(objectFilter, FireBaseCloudMessage::class.java)
            Log.d("TAG", "onMessageReceived: NamTD8 dsabdhsajdbj:${appCache.token}")
            if (appCache.token.isEmpty() || cloudMessage.data.salon_id != userLocalSource.getSalonID()) {
                return
            }
            NotificationsManager(applicationContext).defaultNotify(cloudMessage)
            appEvent.notifyCloudMessage.post(cloudMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        appCache.deviceToken = p0
    }
}