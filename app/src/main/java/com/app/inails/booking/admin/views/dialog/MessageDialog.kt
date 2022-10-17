package com.app.inails.booking.admin.views.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogMessageBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.views.main.dialogs.NotifyDialog

open class MessageDialog(context: Context) : BaseDialog(context) {
    private var mOnDismiss: () -> Unit = {}
    private val binding = viewBinding(DialogMessageBinding::inflate)

    init {
        binding.btnDismiss.onClick {
            dismiss()
            mOnDismiss()
        }
    }

    fun show(title: String, message: String, onDismiss: () -> Unit = {}) {
        mOnDismiss = onDismiss
        binding.txtBody.show(message.isNotBlank()) {
            text = message
        }
        binding.txtTitle.show(title.isNotBlank()) {
            text = title
        }
        super.show()
    }

    override fun show() {
        error("Not support")
    }
}
interface MessageDialogOwner : ViewScopeOwner {
    val messageDialog: MessageDialog
        get() = with(viewScope) {
            getOr("message:dialog") { MessageDialog(context) }
        }
}