package com.app.inails.booking.admin.repository.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.NotificationApi
import com.app.inails.booking.admin.datasource.remote.clients.NotificationClientApi
import com.app.inails.booking.admin.factory.NotificationFactory
import com.app.inails.booking.admin.factory.client.NotificationClientFactory

@Inject(ShareScope.Fragment)
class NotificationRepository(
    private val notificationApi: NotificationClientApi,
    private val notificationFactory: NotificationClientFactory,
    private val localSource: UserLocalSource
) {

    suspend fun totalUnread(): Int {
        if (!localSource.isLogin()) return -1
        return notificationApi.totalUnread().await()
    }

    suspend fun totalForTitleNotification(): String {
        if (!localSource.isLogin()) return "0"
        return notificationFactory.createTitleNotify(notificationApi.totalUnread().await())
    }
}