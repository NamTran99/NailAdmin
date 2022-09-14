package com.app.inails.booking.admin.model.popup

import android.content.Context
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.IStaff

class PopUpServiceMore(var id: Int, var name: String, var pos: Int, var service: IService?) {
    override fun toString(): String {
        return name
    }

    companion object {
        const val EDIT_ID = 0
        const val DELETE_ID = 1

        fun mockActive(context: Context): ArrayList<PopUpServiceMore> {
            return arrayListOf(
                PopUpServiceMore(EDIT_ID, context.getString(R.string.btn_edit), 0, null),
                PopUpServiceMore(DELETE_ID, context.getString(R.string.btn_delete), 1, null)
            )
        }
    }
}