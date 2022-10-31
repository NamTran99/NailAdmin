package com.app.inails.booking.admin.views.me.adapters

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogEmailReceiveFeedbackBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner


class CreateUpdateEmailDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogEmailReceiveFeedbackBinding::inflate)

    init {
        setCancelable(false)
        binding.btnClose.onClick {
            confirmDialog.show(
                title = context.getString(R.string.tittle_exit_update_email),
                message = context.getString(R.string.message_exit),
                function = {
                    dismiss()
                }
            )
        }
    }

    fun show(
        function: (String) -> Unit
    ) {
        with(binding) {
            btSave.setOnClickListener {
                function.invoke(etEnterEmail.text.toString())
                dismiss()
            }
        }
        super.show()
    }
}

interface CreateUpdateEmailOwner : ViewScopeOwner {
    val createUpdateEmailDialog: CreateUpdateEmailDialog
        get() = with(viewScope) {
            getOr("CreateEmailFeedback:dialog") { CreateUpdateEmailDialog(context) }
        }
}