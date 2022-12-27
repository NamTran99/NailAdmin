package com.app.inails.booking.admin.views.clients.dialog.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogBookingNotifyBinding
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.client.IBookingNotification
import com.app.inails.booking.admin.views.clients.booking.ServiceSummaryAdapter

class BookingNotificationDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogBookingNotifyBinding::inflate)

    init {
//        setCancelable(false)
//        setCanceledOnTouchOutside(false)
        binding.btnClose.onClick {
            dismiss()
        }
    }

    private fun displays(
        it: IBookingNotification,
        function: () -> Unit = {}
    ) = lock(binding) {
        txtBookingID.text = it.bookingIdDisplay
        txtSalonName.text = it.salonName
        txtStaff.text = it.staffName
        txtDatetime.text = it.dateTime
        ServiceSummaryAdapter(rcvService).submit(it.services)
        binding.btnViewDetails.onClick {
            function()
            dismiss()
        }
    }


    fun showAccepted(
        it: IBookingNotification,
        function: () -> Unit = {}
    ) = with(binding) {
        txtTitle.setText(R.string.title_booking_accepted)
        listOf(labelReason, txtReason, txtDesUpcoming, btnDirection).show(false)
        displays(it, function)
        super.show()
    }

    fun showCancelled(
        it: IBookingNotification,
        function: () -> Unit = {}
    ) = with(binding) {
        txtTitle.setText(R.string.title_booking_cancelled)
        listOf(labelReason, txtReason).show(true)
        listOf(txtDesUpcoming, btnDirection).show(false)
        txtReason.text = it.reason
        displays(it, function)
        super.show()
    }

    fun showNew(
        it: IBookingNotification,
        function: () -> Unit = {}
    ) = with(binding) {
        txtTitle.text = context.getString(R.string.title_booking_new_service_from, it.salonName)
        listOf(labelReason, txtReason, txtDesUpcoming, btnDirection).show(false)
        displays(it, function)
        super.show()
    }

    fun showUpcoming(
        it: IBookingNotification,
        onDetail: () -> Unit = {},
        onDirection: () -> Unit = {},
    ) = with(binding) {
        txtTitle.text = context.getString(R.string.title_booking_upcoming)
        listOf(labelReason, txtReason).show(false)
        listOf(txtDesUpcoming, btnDirection).show(true)
        displays(it, onDetail)
        btnDirection.onClick {
            dismiss()
            onDirection()
        }
        super.show()
    }

    fun showUpdate(
        it: IBookingNotification,
        onDetail: () -> Unit = {}
    ) = with(binding) {
        txtTitle.setText(R.string.title_booking_update)
        listOf(labelReason, txtReason, txtDesUpcoming, btnDirection).show(false)
        displays(it, onDetail)
        super.show()
    }
}

interface BookingNotificationDialogOwner : ViewScopeOwner {
    val acceptedDialog: BookingNotificationDialog
        get() = with(viewScope) {
            getOr("accepted:dialog") { BookingNotificationDialog(context) }
        }

    val cancelledDialog: BookingNotificationDialog
        get() = with(viewScope) {
            getOr("cancelled:dialog") { BookingNotificationDialog(context) }
        }

    val newDialog: BookingNotificationDialog
        get() = with(viewScope) {
            getOr("new:dialog") { BookingNotificationDialog(context) }
        }

    val upcomingDialog: BookingNotificationDialog
        get() = with(viewScope) {
            getOr("upcoming:dialog") { BookingNotificationDialog(context) }
        }

    val updateDialog: BookingNotificationDialog
        get() = with(viewScope) {
            getOr("update:dialog") { BookingNotificationDialog(context) }
        }
}