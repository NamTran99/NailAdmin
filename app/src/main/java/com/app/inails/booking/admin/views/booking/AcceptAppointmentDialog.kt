package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.widget.Toast
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogAcceptAppointmentBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.IStaff


class AcceptAppointmentDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogAcceptAppointmentBinding::inflate)
    var onSelectStaffListener: (() -> Unit?)? = null
    private var staffID: Int? = null
    init {
        binding.btClose.onClick {
            dismiss()
        }

        binding.tvChooseStaff.onClick {
            onSelectStaffListener?.invoke()
        }
    }

    fun show(
        apm : IAppointment,
        function: (Int,Int) -> Unit
    ) {
        staffID = apm.staffID
        with(binding) {
            tvChooseStaff.setText(R.string.label_choose_staff)
            staffLayout.show(apm.staffID == 0)
            spHour.setSelection(0)
            spMinute.setSelection(0)
            btSubmit.onClick {
                if (staffID == null || staffID == 0) {
                    Toast.makeText(context, R.string.error_blank_staff_id, Toast.LENGTH_SHORT)
                        .show()
                    return@onClick
                }
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
                    time, staffID!!
                )
            }
        }
        super.show()
    }

    fun updateStaff(staff: IStaff) {
        staffID = staff.id
        binding.tvChooseStaff.text = staff.name
    }
}

interface AcceptAppointmentOwner : ViewScopeOwner {
    val acceptAppointmentDialog: AcceptAppointmentDialog
        get() = with(viewScope) {
            getOr("accept_appointment:dialog") { AcceptAppointmentDialog(context) }
        }
}