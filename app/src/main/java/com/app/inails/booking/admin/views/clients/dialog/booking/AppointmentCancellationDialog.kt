package com.app.inails.booking.admin.views.clients.dialog.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogApmCancellationBinding
import com.app.inails.booking.admin.extention.onClick


class AppointmentCancellationDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogApmCancellationBinding::inflate)

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.btnBack.onClick {
            dismiss()
        }
    }

    fun show(function: (String) -> Unit = {}) {
        binding.edtContents.setText("")
        binding.btnDismiss.onClick {
            val reason = binding.edtContents.text.toString()
            if (reason.isEmpty())
                binding.edtContents.error = context.getString(R.string.error_blank_apm_reason)
            else if (reason.trim().length < 3) {
                binding.edtContents.error = context.getString(R.string.error_length_apm_reason)
            } else {
                function(reason)
                dismiss()
            }
        }
        super.show()
    }
}

interface ApmCancelDialogOwner : ViewScopeOwner {
    val apmCancelDialog: AppointmentCancellationDialog
        get() = with(viewScope) {
            getOr("apm:cancel:dialog") { AppointmentCancellationDialog(context) }
        }
}