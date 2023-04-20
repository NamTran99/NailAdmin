package com.app.inails.booking.admin.views.management.staff

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogMessageBinding
import com.app.inails.booking.admin.databinding.DialogStaffNoticeBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.views.dialog.MessageDialog
import com.app.inails.booking.admin.views.main.dialogs.NotifyDialog

class StaffNoticeDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogStaffNoticeBinding::inflate)

    init {
        binding.btnDismiss.onClick {
            dismiss()
        }
    }
}
interface StaffNoticeDialogOwner : ViewScopeOwner {
    val staffNoticeDialog: StaffNoticeDialog
        get() = with(viewScope) {
            getOr("staffNotice:dialog") { StaffNoticeDialog(context) }
        }
}