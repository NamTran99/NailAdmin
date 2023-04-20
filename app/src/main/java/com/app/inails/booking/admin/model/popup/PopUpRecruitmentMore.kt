package com.app.inails.booking.admin.model.popup

import android.content.Context
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.popup.PopUpCustomerMore.Companion.BOOKING_LIST
import com.app.inails.booking.admin.model.ui.IRecruitment
import com.app.inails.booking.admin.model.ui.IStaff

class PopUpRecruitmentMore(var id: Int, var name: String, var pos: Int, var user: IRecruitment?) {
    override fun toString(): String {
        return name
    }

    companion object {
        const val ID_EDIT = 0
        const val ID_HIDE = 1
        const val ID_PUBLISH = 2
        const val ID_DELETE = 3
        fun mockRecruitmentMenu(context: Context, isPublish: Boolean): ArrayList<PopUpRecruitmentMore> {
            return arrayListOf(
                PopUpRecruitmentMore(ID_EDIT, context.getString(R.string.btn_edit_1), 1, null),
                PopUpRecruitmentMore(if(isPublish)ID_HIDE else ID_PUBLISH, if(isPublish)context.getString(R.string.hide_recruitment) else context.getString(R.string.show_recruitment), 2, null),
                PopUpRecruitmentMore(ID_DELETE, context.getString(R.string.delete_recruitment), 3, null),
            )
        }
    }
}