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
                if (spHour.selectedItemPosition == 0 && spMinute.selectedItemPosition == 0) {
                    Toast.makeText(
                        context,
                        R.string.error_empty_duration_time_to_service,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@onClick
                }
                val hours = (spHour.selectedItem as String).toInt()
                val minutes = (spMinute.selectedItem as String).toInt()
                val time = (hours * 60) + minutes
                function.invoke(
                    time
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