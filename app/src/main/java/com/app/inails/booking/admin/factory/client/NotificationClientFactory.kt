package com.app.inails.booking.admin.factory.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.remote.clients.NotificationClientApi
import com.app.inails.booking.admin.exception.toDateLocal
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.NotificationDTO
import com.app.inails.booking.admin.model.response.client.NotificationClientDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.INotification
import com.app.inails.booking.admin.model.ui.NotificationImpl
import com.app.inails.booking.admin.model.ui.client.INotificationClient
import com.app.inails.booking.admin.model.ui.client.NotificationClientImpl

@Inject(ShareScope.Singleton)
class NotificationClientFactory(private val textFormatter: TextFormatter) {

    private fun create(item: NotificationClientDTO): INotificationClient {
        return object : INotificationClient, ISelector by NotificationClientImpl() {
            override val id: Long
                get() = item.id.safe()
            override val bookingID: Long
                get() = item.data_id.safe()
            override val salonID: Long
                get() = item.sender?.id.safe()
            override val content: String
                get() = item.body.safe()
            override val datetime: String
                get() = item.created_at_timestamp?.toDateLocal().safe()
            override val isRead: Boolean
                get() = item.is_read == 1
            override val textColor: Int
                get() = textFormatter.colorTextNotification(item.is_read)
            override val type: Int
                get() = item.type?:0
        }
    }

    fun createList(items: List<NotificationClientDTO>): List<INotificationClient> {
        return items.map(::create)
    }

    fun createTitleNotify(totalUnread: Int): String {
        return textFormatter.titleNotificationWithUnread(totalUnread)
    }

}