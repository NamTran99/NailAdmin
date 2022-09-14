package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.widget.Toast
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCancelAppointmentBinding
import com.app.inails.booking.admin.extention.onClick


class CancelAppointmentDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogCancelAppointmentBinding::inflate)

    fun show(
        function: (Int, String) -> Unit
    ) {
        with(binding) {
            btSubmit.onClick {
                val reason = etReason.text.toString().trim()
                if (reason.isEmpty()) {
                    Toast.makeText(context, R.string.error_empty_reason, Toast.LENGTH_SHORT).show()
                    return@onClick
                }
                val cancelBy =
                    if (rgCancel.checkedRadioButtonId == R.id.rbByGuest)
                        DataConst.CancelAppointmentBy.CLIENT
                    else DataConst.CancelAppointmentBy.ADMIN_SALON
                function.invoke(
                    cancelBy, reason
                )
            }
        }
        super.show()
    }
}

interface CancelAppointmentOwner : ViewScopeOwner {
    val cancelAppointmentDialog: CancelAppointmentDialog
        get() = with(viewScope) {
            getOr("cancel_appointment:dialog") { CancelAppointmentDialog(context) }
        }
}