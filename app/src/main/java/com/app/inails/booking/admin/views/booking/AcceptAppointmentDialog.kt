package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.widget.Toast
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogAcceptAppointmentBinding
import com.app.inails.booking.admin.extention.onClick


class AcceptAppointmentDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogAcceptAppointmentBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        function: (Int) -> Unit
    ) {
        with(binding) {
            btSubmit.onClick {
                val time = etServiceTime.text.toString().toIntOrNull()
                if (time == null) {
                    Toast.makeText(context, R.string.error_empty_service_time, Toast.LENGTH_SHORT)
                        .show()
                    return@onClick
                }
                if (time == 0) {
                    Toast.makeText(
                        context,
                        R.string.error_service_time_greater_than,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@onClick
                }
                function.invoke(
                    time.toInt()
                )
            }
        }
        super.show()
    }
}

interface AcceptAppointmentOwner : ViewScopeOwner {
    val acceptAppointmentDialog: AcceptAppointmentDialog
        get() = with(viewScope) {
            getOr("accept_appointment:dialog") { AcceptAppointmentDialog(context) }
        }
}