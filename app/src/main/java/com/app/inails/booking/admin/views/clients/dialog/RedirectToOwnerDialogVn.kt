package com.app.inails.booking.admin.views.clients.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCreateUpdateServiceBinding
import com.app.inails.booking.admin.databinding.DialogRedirectToOwnerBinding
import com.app.inails.booking.admin.databinding.DialogRedirectToOwnerVnBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.views.dialog.MessageDialog
import com.bumptech.glide.Glide.init

class RedirectToOwnerDialogVn(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogRedirectToOwnerVnBinding::inflate)

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

    fun show(function:((String) -> Unit)){
        binding.apply {
            etPassword.setText("")
            btnSubmit.onClick {
                if(password.isBlank()){
                    etPassword.error = context.getString(R.string.error_blank_password_vn)
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

interface RedirectToOwnerDialogVnOwner : ViewScopeOwner {
    val redirectToOwnerDialogVn: RedirectToOwnerDialogVn
        get() = with(viewScope) {
            getOr("redirectToOwnerDialogVn:dialog") { RedirectToOwnerDialogVn(context) }
        }
}