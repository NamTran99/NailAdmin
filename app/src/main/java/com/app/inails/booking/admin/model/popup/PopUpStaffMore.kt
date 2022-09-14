package com.app.inails.booking.admin.model.popup

import android.content.Context
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.ui.IStaff

class PopUpStaffMore(var id: Int, var name: String, var pos: Int, var user: IStaff?) {
    override fun toString(): String {
        return name
    }

    companion object {
        fun mockActive(context: Context): ArrayList<PopUpStaffMore> {
            return arrayListOf(
                PopUpStaffMore(0, context.getString(R.string.btn_edit), 0, null),
                PopUpStaffMore(1, context.getString(R.string.btn_inactive), 1, null),
                PopUpStaffMore(3, context.getString(R.string.btn_order_history), 2, null),
                PopUpStaffMore(4, context.getString(R.string.btn_attendance_history), 3, null),
                PopUpStaffMore(5, context.getString(R.string.btn_delete), 4, null)
            )
        }

        fun mockInactive(context: Context): ArrayList<PopUpStaffMore> {
            return arrayListOf(
                PopUpStaffMore(0, context.getString(R.string.btn_edit), 0, null),
                PopUpStaffMore(2, context.getString(R.string.btn_active), 1, null),
                PopUpStaffMore(3, context.getString(R.string.btn_order_history), 2, null),
                PopUpStaffMore(4, context.getString(R.string.btn_attendance_history), 3, null),
                PopUpStaffMore(5, context.getString(R.string.btn_delete), 4, null)
            )
        }

        fun mockIsWorking(context: Context): ArrayList<PopUpStaffMore> {
            return arrayListOf(
                PopUpStaffMore(0, context.getString(R.string.btn_edit), 0, null),
                PopUpStaffMore(3, context.getString(R.string.btn_order_history), 2, null),
                PopUpStaffMore(4, context.getString(R.string.btn_attendance_history), 3, null),
            )
        }
    }
}