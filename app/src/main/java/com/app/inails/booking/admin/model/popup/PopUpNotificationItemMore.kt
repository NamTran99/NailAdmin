package com.app.inails.booking.admin.model.popup

import android.content.Context
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.ui.INotification

class PopUpNotificationItemMore(
    var id: Int,
    var name: String,
    var pos: Int,
    var notification: INotification?
) {
    override fun toString(): String {
        return name
    }

    companion object {
        fun mockUnread(context: Context): ArrayList<PopUpNotificationItemMore> {
            return arrayListOf(
                PopUpNotificationItemMore(0, context.getString(R.string.btn_read), 0, null),
                PopUpNotificationItemMore(2, context.getString(R.string.btn_delete), 0, null),
            )
        }

        fun mockRead(context: Context): ArrayList<PopUpNotificationItemMore> {
            return arrayListOf(
                PopUpNotificationItemMore(1, context.getString(R.string.btn_unread), 0, null),
                PopUpNotificationItemMore(2, context.getString(R.string.btn_delete), 0, null),
            )
        }
    }
}