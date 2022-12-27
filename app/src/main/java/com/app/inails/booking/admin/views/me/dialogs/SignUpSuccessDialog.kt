package com.app.inails.booking.admin.views.me.dialogs

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogSignUpSuccessBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show

 class SignUpSuccessDialog(context: Context) : BaseDialog(context) {
    private var mOnDismiss: () -> Unit = {}
    private val binding = viewBinding(DialogSignUpSuccessBinding::inflate)

    init {
        setCancelable(false)
        binding.btnDismiss.onClick {
            dismiss()
            mOnDismiss.invoke()
        }
    }

    fun show(onDismiss: () -> Unit = {}) {
        mOnDismiss = onDismiss
        super.show()
    }
}
interface SignUpSuccessDialogOwner : ViewScopeOwner {
    val signupDialog: SignUpSuccessDialog
        get() = with(viewScope) {
            getOr("signup:dialog") { SignUpSuccessDialog(context) }
        }
}