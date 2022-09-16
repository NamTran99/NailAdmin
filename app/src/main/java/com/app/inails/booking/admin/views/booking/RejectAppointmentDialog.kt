package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.widget.Toast
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogRejectAppointmentBinding
import com.app.inails.booking.admin.extention.onClick


class RejectAppointmentDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogRejectAppointmentBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        @StringRes title: Int,
        function: (String) -> Unit
    ) {
        with(binding) {
            tvTitle.setText(title)
            btSubmit.onClick {
                val reason = etReason.text.toString().trim()
                if (reason.isEmpty()) {
                    Toast.makeText(context, R.string.error_empty_reason, Toast.LENGTH_SHORT)
                        .show()
                    return@onClick
                }

                function.invoke(
                    reason
                )
            }
        }
        super.show()
    }
}

interface RejectAppointmentOwner : ViewScopeOwner {
    val rejectAppointmentDialog: RejectAppointmentDialog
        get() = with(viewScope) {
            getOr("reject_appointment:dialog") { RejectAppointmentDialog(context) }
        }
}