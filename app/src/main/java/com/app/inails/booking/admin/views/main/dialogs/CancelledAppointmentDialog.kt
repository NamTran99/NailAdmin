package com.app.inails.booking.admin.views.main.dialogs

import android.content.Context
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCancelledAppointmentBinding
import com.app.inails.booking.admin.model.ui.IAppointment

class CancelledAppointmentDialog(context: Context): BaseDialog(context) {
    private val binding = viewBinding(DialogCancelledAppointmentBinding::inflate)

    fun show(appointment: IAppointment, typeDialog: Int){
//        with(binding)
    }
}