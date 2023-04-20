package com.app.inails.booking.admin.views.booking.create_appointment.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.CreateCustomerDialogBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner


class CreateCustomerDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(CreateCustomerDialogBinding::inflate)

    init {
        setCancelable(false)
        setBackGroundBLur()
        binding.apply {
            etPhone.inputTypePhoneUS()
            btClose.setOnClickListener {
                dismiss()
            }
        }
    }

    fun show(
        textSearch: String,
        onSubmit: (phone: String, name: String) -> Unit = { _, _ -> }
    ) {
        with(binding) {
            etPhone.clearText()
            etFullName.clearText()
            if (textSearch.isNumber()) {
                etPhone.setText(textSearch)
            } else {
                etFullName.setText(textSearch)
            }
            btSubmit.onClick {
                onSubmit.invoke(etPhone.getTextString(), etFullName.getTextString())
            }
        }
        super.show()
    }
}

interface CreateCustomerDialogOwner : ViewScopeOwner {
    val createCustomerDialog: CreateCustomerDialog
        get() = with(viewScope) {
            getOr("createCustomerDialog:dialog") { CreateCustomerDialog(context) }
        }
}