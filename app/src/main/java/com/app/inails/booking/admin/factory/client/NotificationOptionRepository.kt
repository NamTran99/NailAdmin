package com.app.inails.booking.admin.factory.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.datasource.remote.NotificationApi
import com.app.inails.booking.admin.datasource.remote.clients.NotificationClientApi
import com.app.inails.booking.admin.model.ui.client.popup.INotificationItemOption
import com.app.inails.booking.admin.model.ui.client.popup.INotificationOption

@Inject(ShareScope.Fragment)
class NotificationOptionRepository(
    private val notificationApi: NotificationClientApi
) {
    fun general(): List<INotificationOption> = DataConst.Mock.notificationOptions

    fun item(isRead: Boolean): List<INotificationItemOption> {
        return if (isRead) DataConst.Mock.notificationItemUnreadOptions
        else DataConst.Mock.notificationItemReadOptions
    }

    suspend fun readAll() {
        notificationApi.readAll().await()
    }

    suspend fun deleteAll() {
        notificationApi.deleteAll().await()
    }

    suspend fun read(id: Long) {
        notificationApi.read(id).await()
    }

    suspend fun unread(id: Long) {
        notificationApi.unread(id).await()
    }

    suspend fun delete(id: Long) {
        notificationApi.delete(listOf(id).toString()).await()
    }
}