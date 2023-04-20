package com.app.inails.booking.admin.model.popup

import android.content.Context
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.ui.IStaff

class PopUpCustomerMore(var id: Int, var name: String, var pos: Int, var user: IStaff?) {
    override fun toString(): String {
        return name
    }

    companion object {
        const val EDIT_ID = 0
        const val BOOKING_LIST = 1
        fun mockCustomerMenu(context: Context): ArrayList<PopUpCustomerMore> {
            return arrayListOf(
                PopUpCustomerMore(EDIT_ID, context.getString(R.string.btn_edit), EDIT_ID, null),
                PopUpCustomerMore(BOOKING_LIST, context.getString(R.string.booking_list), BOOKING_LIST, null),
            )
        }
    }
}