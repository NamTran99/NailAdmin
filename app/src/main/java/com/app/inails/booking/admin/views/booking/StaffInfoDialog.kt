package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCustomerInfoBinding
import com.app.inails.booking.admin.databinding.DialogStaffInfoBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IStaff


class StaffInfoDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogStaffInfoBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        staff: IStaff
    ) {
        with(binding) {
            tvStaffName.text = staff.name
            tvPhone.text = staff.phone
        }
        super.show()
    }

}

interface StaffInfoDialogOwner : ViewScopeOwner {
    val staffInfoDialog: StaffInfoDialog
        get() = with(viewScope) {
            getOr("staff_info:dialog") { StaffInfoDialog(context) }
        }
}