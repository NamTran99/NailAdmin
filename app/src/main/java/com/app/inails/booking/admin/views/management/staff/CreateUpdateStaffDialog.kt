package com.app.inails.booking.admin.views.management.staff

import android.content.Context
import android.support.core.view.ViewScopeOwner
import androidx.annotation.StringRes
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCreateUpdateStaffBinding
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IStaff


class CreateUpdateStaffDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogCreateUpdateStaffBinding::inflate)

    init {
        binding.etStaffPhone.inputTypePhoneUS()
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        @StringRes title: Int,
        staff: IStaff? = null,
        function: (String, String, String) -> Unit
    ) {
        with(binding) {
            tvTitle.setText(title)
            etStaffFirstName.setText("")
            etStaffLastName.setText("")
            etStaffPhone.setText("")

            staff?.let {
                etStaffFirstName.setText(it.firstName)
                etStaffLastName.setText(it.lastName)
                val hasCountryCode = it.phone.indexOf("1") == 0
                var phone = it.phone.replace("-", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(" ", "").replace("+1", "").trim()
                if (hasCountryCode) phone = phone.substring(1)
                etStaffPhone.setText(phone)
            }

            btSubmit.onClick {
                function.invoke(
                    etStaffFirstName.text.toString(),
                    etStaffLastName.text.toString(),
                    etStaffPhone.text.toString()
                )
            }
        }
        super.show()
    }
}

interface CreateUpdateStaffOwner : ViewScopeOwner {
    val createUpdateStaffDialog: CreateUpdateStaffDialog
        get() = with(viewScope) {
            getOr("staff:dialog") { CreateUpdateStaffDialog(context) }
        }
}