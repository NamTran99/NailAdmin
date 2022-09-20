package com.app.inails.booking.admin.views.me.adapters

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogEmailReceiveFeedbackBinding
import com.app.inails.booking.admin.extention.onClick


class CreateUpdateEmailDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogEmailReceiveFeedbackBinding::inflate)

    init {
        binding.btnClose.onClick {
            dismiss()
        }
    }

    fun show(
        function: (String) -> Unit
    ) {
        with(binding) {
            btSave.setOnClickListener {
                function.invoke(etEnterEmail.text.toString())
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