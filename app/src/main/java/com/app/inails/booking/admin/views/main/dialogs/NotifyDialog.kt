package com.app.inails.booking.admin.views.main.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.livedata.post
import android.support.core.view.ViewScopeOwner
import android.support.di.inject
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.CUSTOMER_CANCEL_APPOINTMENT
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.CUSTOMER_CREATE_APPOINTMENT
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.CUSTOMER_FEEDBACK
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.NotifyDialogAppointmentBinding
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.firebase.Data
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.views.report.adapters.ReportServiceAdapter

class NotifyDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(NotifyDialogAppointmentBinding::inflate)
    private var appointment = Data()
    private var onClickViewDetailAppointment: ((Int) -> Unit)? = null
    var onReadNotiListener: ((Int) -> Unit)? = null
    private val appEvent by inject<AppEvent>()

    init {
        setCancelable(false)
    }

    @SuppressLint("SetTextI18n")
    fun show(
        fireBaseCloudMessage: FireBaseCloudMessage,
        onClickViewDetailAppointment: ((Int) -> Unit)
    ) {
        this.onClickViewDetailAppointment = onClickViewDetailAppointment
        adjustViewVisibility(fireBaseCloudMessage.type.toInt())
        setUpListener()

        appointment = fireBaseCloudMessage.data
        with(binding) {
            tvBookingID.text = "#${appointment.id}"
            tvCustomerName.text = appointment.customer_name?.displaySafe()
            tvPhone.text = appointment.customer_phone?.formatPhoneUS().displaySafe1()
            tvStaffName.text = appointment.staff_name.displaySafe1()
            tvDateTime.text = appointment.date_appointment?.formatDateAppointment().displaySafe1()
            tvReason.text = appointment.reason_cancel
            tvFeedbackContent.text = appointment.content_feedback.safe().replace("\\n", "\n").trim()
            tvFeedbackContent.show(tvFeedbackContent.text.isNotEmpty())
            ratingBar.rating = appointment.rating.safe().toFloat()

            val listService = appointment.services.map {
                object : IService by ServiceImpl() {
                    override val id: Int
                        get() = it.id.safe()
                    override val name: String
                        get() = it.name.safe()
                    override val price: Double
                        get() = it.price.toDoubleOrNull() ?: 0.0
                }
            }

            ReportServiceAdapter(rvServices).submit(listService)
        }
        super.show()
    }

    private fun adjustViewVisibility(type: Int) {
        with(binding) {
            (type == CUSTOMER_CREATE_APPOINTMENT ||
                    type == CUSTOMER_CANCEL_APPOINTMENT).apply {
                show(btViewDetail)
                show(tvClose)
            }
            (type == CUSTOMER_FEEDBACK).apply {
                show(btClose)
                show(tvDescription)
                show(lvRating)
            }
            (type == CUSTOMER_CANCEL_APPOINTMENT).apply {
                show(lvReason)
            }

            when (type) {
                CUSTOMER_CREATE_APPOINTMENT -> R.string.dialog_notify_create_title
                CUSTOMER_CANCEL_APPOINTMENT -> R.string.dialog_notify_cancel_title
                CUSTOMER_FEEDBACK -> R.string.dialog_notify_feedback_title
                else -> null
            }?.let {
                tvTitle.text = context.getString(it)
            }
        }
    }

    private fun setUpListener() {
        with(binding) {
            btClose.setOnClickListener {
                onReadNotiListener?.invoke(appointment.id.safe())
                dismissAndRefreshData()
            }
            tvClose.setOnClickListener {
                onReadNotiListener?.invoke(appointment.id.safe())
                dismissAndRefreshData()
            }
            btViewDetail.setOnClickListener {
                onReadNotiListener?.invoke(appointment.id.safe())
                onClickViewDetailAppointment?.invoke(appointment.id.safe())
                dismissAndRefreshData()
            }
        }
    }

    private fun dismissAndRefreshData() {
        appEvent.refreshData.post(true)
        dismiss()
    }
}

interface NotifyDialogOwner : ViewScopeOwner {
    val notifyDialog: NotifyDialog
        get() = with(viewScope) {
            getOr("finish_booking:dialog") { NotifyDialog(context) }
        }
}