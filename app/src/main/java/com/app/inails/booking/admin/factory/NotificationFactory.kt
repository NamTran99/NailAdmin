package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.extention.toCreatedAt
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.NotificationDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.INotification
import com.app.inails.booking.admin.model.ui.NotificationImpl

@Inject(ShareScope.Singleton)
class NotificationFactory(private val textFormatter: TextFormatter) {

    private fun createNotification(notiDTO: NotificationDTO): INotification {
        return object : INotification, ISelector by NotificationImpl() {
            override val id: Int
                get() = notiDTO.id.safe()
            override val body: String
                get() = notiDTO.body.safe()
            override val title: String
                get() = notiDTO.title.safe()
            override val isRead: Boolean
                get() = notiDTO.is_read == 1
            override val createdDate: String
                get() = notiDTO.created_at.toCreatedAt()
            override val color: Int
                get() = textFormatter.formatColorNotification(notiDTO.is_read)
            override val dataId: Int
                get() = notiDTO.data_id
            override val type: Int
                get() = notiDTO.type?:0
        }
    }

    fun createNotificationList(notifications: List<NotificationDTO>): List<INotification> {
        return notifications.map(::createNotification)
    }

    fun createANotification(notiDTO: NotificationDTO): INotification {
        return createNotification(notiDTO)
    }
}