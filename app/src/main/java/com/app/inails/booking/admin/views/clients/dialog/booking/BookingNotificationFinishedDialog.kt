package com.app.inails.booking.admin.views.clients.dialog.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogBookingFinishedBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.client.IBookingNotification
import com.app.inails.booking.admin.views.clients.booking.ServiceSummaryAdapter


class BookingNotificationFinishedDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogBookingFinishedBinding::inflate)

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun show(
        item: IBookingNotification,
        onDone: () -> Unit = {},
        onFeedback: (Long) -> Unit = {}
    ) = with(binding) {
        txtSalonName.text = item.salonName
        txtBookingID.text = item.bookingIdDisplay
        txtTotal.text = item.totalPrice
        viewInfo.apply {
            txtSalonName.text = item.salonName
            txtClientName.text = item.customerName
            txtPhoneNumber.text = item.customerPhone
            txtStaff.text = item.staffName
        }
        ServiceSummaryAdapter(rcvService).submit(item.services)
        txtNote.show(item.notes.isNotEmpty())
        txtNote.text = item.notes
        btnDone.onClick {
            dismiss()
            onDone()
        }
        btnFeedBack.onClick {
            dismiss()
            onFeedback(item.bookingID)
        }
        super.show()
    }
}

interface BookingNotificationFinishedDialogOwner : ViewScopeOwner {
    val finishedDialog: BookingNotificationFinishedDialog
        get() = with(viewScope) {
            getOr("finished:dialog") { BookingNotificationFinishedDialog(context) }
        }
}