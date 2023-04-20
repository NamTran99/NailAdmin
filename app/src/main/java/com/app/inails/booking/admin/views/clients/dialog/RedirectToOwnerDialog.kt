package com.app.inails.booking.admin.views.clients.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogRedirectToOwnerBinding
import com.app.inails.booking.admin.extention.onClick

class RedirectToOwnerDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogRedirectToOwnerBinding::inflate)

    val password: String
        get() = binding.etPassword.text.toString()

    init {
        setCancelable(false)
        binding.apply {
            btClose.onClick {
                dismiss()
            }
        }
    }

    fun show(function: ((String) -> Unit)) {
        binding.apply {
            etPassword.setText("")
            btnSubmit.onClick {
                if (password.isBlank()) {
                    etPassword.error = context.getString(R.string.error_blank_password)
                    etPassword.requestFocus()
                    return@onClick
                }
                function.invoke(etPassword.text.toString())
                dismiss()
            }
        }
        super.show()
    }
}

interface RedirectToOwnerDialogOwner : ViewScopeOwner {
    val redirectToOwnerDialog: RedirectToOwnerDialog
        get() = with(viewScope) {
            getOr("redirectToOwner      :dialog") { RedirectToOwnerDialog(context) }
        }
}