package com.app.inails.booking.admin.service

import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.di.inject
import android.util.Log
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.CUSTOMER_CANCEL_APPOINTMENT
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.CUSTOMER_CREATE_APPOINTMENT
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.CUSTOMER_FEEDBACK
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.OWNER_ACCOUNT_ACTIVE
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.OWNER_ACCOUNT_APPROVE
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.OWNER_ACCOUNT_INACTIVE
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.extention.toObject
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessageClient
import com.app.inails.booking.admin.notification.NotificationsManager
import com.app.inails.booking.admin.notification.NotificationsManagerClient
import com.google.android.youtube.player.internal.e
import com.google.android.youtube.player.internal.i
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
            val cloudMessageClient = `object`.toString().toObject<FireBaseCloudMessageClient>()
            Log.d("TAG", "onMessageReceived: namtd8 ${cloudMessage.type}")
            when(cloudMessage.type.toInt()){
                OWNER_ACCOUNT_APPROVE ->{
                    NotificationsManager(applicationContext).defaultNotify(cloudMessage)
                    userLocalSource.approveUser()
                    appEvent.notifyAccountApproved.post(true)
                    appEvent.notifyAccountApprovedAccount.post(true)
                }
                OWNER_ACCOUNT_ACTIVE, OWNER_ACCOUNT_INACTIVE -> return
                CUSTOMER_CREATE_APPOINTMENT,CUSTOMER_CANCEL_APPOINTMENT, CUSTOMER_FEEDBACK ->{
                    if(cloudMessage.data == null || appCache.token.isEmpty() || cloudMessage.data.salon_id != userLocalSource.getSalonID() || userLocalSource.getUserClientDto() != null) {
                        return
                    }
                    NotificationsManager(applicationContext).defaultNotify(cloudMessage)
                    appEvent.notifyCloudMessage.post(cloudMessage)
                }
                else ->{
                    if(userLocalSource.getUserClientDto() != null){
                        NotificationsManagerClient(applicationContext).defaultNotify(cloudMessageClient)
                        with(appEvent){
                            notifyCloudMessageClient.post(cloudMessageClient)
                            notifyForAppointment.post(cloudMessageClient)
                            notifyFetchTotal.call()
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.d("TAG", "onMessageReceived-error: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        appCache.deviceToken = p0
    }
}