package com.app.inails.booking.admin.views.management.service

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.text.InputFilter
import androidx.annotation.StringRes
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCreateUpdateServiceBinding
import com.app.inails.booking.admin.databinding.DialogCreateUpdateStaffBinding
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffDialog
import com.app.inails.booking.admin.views.widget.DecimalDigitsInputFilter


class CreateUpdateServiceDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogCreateUpdateServiceBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        @StringRes title: Int,
        service: IService?=null,
        function: (String, String) -> Unit
    ) {
        with(binding) {
            tvTitle.setText(title)
            etPrice.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(4, 2))
            service?.let {
                etServiceName.setText(it.name)
                etPrice.setText(it.price.toString())
            }
            btSubmit.onClick {
                function.invoke(
                    etServiceName.text.toString(),
                    etPrice.text.toString(),
                )
            }
        }
        super.show()
    }
}

interface CreateUpdateServiceOwner : ViewScopeOwner {
    val createUpdateServiceDialog: CreateUpdateServiceDialog
        get() = with(viewScope) {
            getOr("service:dialog") { CreateUpdateServiceDialog(context) }
        }
}