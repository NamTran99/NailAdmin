package com.app.inails.booking.admin.views.clients

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogProfileUpdateBinding
import com.app.inails.booking.admin.extention.isEmail
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.client.UpdateProfileForm
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog2

class UserUpdateInfoDialog(context: BaseActivity) : BaseDialog(context) {

    private val binding = viewBinding(DialogProfileUpdateBinding::inflate)
    private var onSubmitListener: ((UpdateProfileForm) -> Unit)? = null
    private var onBackListener: (() -> Unit)? = null
    private val datePickerDialog by lazy { DatePickerDialog2(context) }
    val form = UpdateProfileForm()

    init {
        setCancelable(false)
        binding.apply {
            datePickerDialog.apply {
                enableDateOfBirth()
                setupClickWithView(edtPfUpdateDob)
            }
            btnSubmit.onClick { validate() }
            btnClose.onClick { onBackListener?.invoke() }
        }
    }

    private fun validate() = with(binding) {
        form.apply {
            name = edtPfUpdateName.text.toString()
            email = edtPfUpdateEmail.text.toString()
            address = edtPfUpdateAddress.text.toString()
            dobOrigin = edtPfUpdateDob.text.toString()
            if (!dobOrigin.isNullOrEmpty())
                dob = edtPfUpdateDob.tag.toString()
        }
        if (form.name.isBlank()) {
            edtPfUpdateName.error = context.getString(R.string.error_blank_name)
            return@with
        }
        if (form.email.isNotBlank() && !form.email.isEmail()) {
            edtPfUpdateEmail.error = context.getString(R.string.error_not_correct_email)
            return@with
        }
        onSubmitListener?.invoke(form)
    }

    fun onSubmitListener(onResult: (UpdateProfileForm) -> Unit = {}, onBack: () -> Unit = {}) {
        onSubmitListener = onResult
        onBackListener = onBack
    }

    fun show(phoneNumber: String) {
        form.phone = phoneNumber
        super.show()
    }
}