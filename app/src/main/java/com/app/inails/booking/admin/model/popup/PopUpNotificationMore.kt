package com.app.inails.booking.admin.model.popup

import android.content.Context
import com.app.inails.booking.admin.R

class PopUpNotificationMore(
    var id: Int,
    var name: String
) {
    override fun toString(): String {
        return name
    }

    companion object {
        fun mock(context: Context): ArrayList<PopUpNotificationMore> {
            return arrayListOf(
                PopUpNotificationMore(0, context.getString(R.string.btn_read_all)),
                PopUpNotificationMore(1, context.getString(R.string.btn_delete_all)),
            )
        }
        fun mockUnselect(context: Context): ArrayList<PopUpNotificationMore> {
            return arrayListOf(
                PopUpNotificationMore(2, context.getString(R.string.btn_select)),
                PopUpNotificationMore(0, context.getString(R.string.btn_read_all)),
                PopUpNotificationMore(1, context.getString(R.string.btn_delete_all)),
            )
        }

        fun mockSelect(context: Context): ArrayList<PopUpNotificationMore> {
            return arrayListOf(
                PopUpNotificationMore(3, context.getString(R.string.btn_unselect_all)),
                PopUpNotificationMore(4, context.getString(R.string.btn_delete_selected)),
            )
        }
    }
}