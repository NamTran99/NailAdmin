package com.app.inails.booking.admin.views.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.databinding.ItemAppointmentBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class AppointmentAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IAppointment, ItemAppointmentBinding>(view) {
    var onClickItemListener: ((IAppointment) -> Unit)? = null
    var onClickFinishListener: ((IAppointment) -> Unit)? = null
    var onClickCancelListener: ((IAppointment) -> Unit)? = null
    var onClickRemoveListener: ((IAppointment) -> Unit)? = null
    var onClickCusWalkInListener: ((IAppointment) -> Unit)? = null
    var onClickHandleListener: ((IAppointment, Int) -> Unit)? = null
    var onClickStartServiceListener: ((IAppointment) -> Unit)? = null
    var onClickCustomerListener: ((ICustomer) -> Unit)? = null
    var onClickRemindListener: ((IAppointment) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemAppointmentBinding {
        return parent.bindingOf(ItemAppointmentBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IAppointment,
        binding: ItemAppointmentBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            root.onClick {
                onClickItemListener?.invoke(item)
            }

            btCancel.setOnClickListener {
                onClickCancelListener?.invoke(item)
            }

            btRemove.setOnClickListener {
                onClickRemoveListener?.invoke(item)
            }

            btWalkIn.setOnClickListener {
                onClickCusWalkInListener?.invoke(item)
            }
            btAccept.setOnClickListener {
                onClickHandleListener?.invoke(item, 1)
            }

            btReject.setOnClickListener {
                onClickHandleListener?.invoke(item, 0)
            }

            btStartService.setOnClickListener {
                onClickStartServiceListener?.invoke(item)
            }

            btFinish.setOnClickListener {
                onClickFinishListener?.invoke(item)
            }

            tvFullName.setOnClickListener {
                onClickCustomerListener?.invoke(item.customer!!)
            }

            btReminder.onClick {
                onClickRemindListener?.invoke(item)
            }

            tvID.text = item.id.formatID()
            tvFullName.text = item.name
            tvTimeAndDate.text = item.dateAppointment
            tvServices.text = item.servicesName
            tvRequest.text = item.staffName
            tvTypeCancel.text = item.canceledBy
            tvPhone.text = item.phone.formatPhoneUS()
            tvStatus.text = item.statusDisplay
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(view.context, item.colorStatus))
            (item.status == DataConst.AppointmentStatus.APM_ACCEPTED && item.type == 2) show acceptLayout + btReminder
            (item.status == DataConst.AppointmentStatus.APM_PENDING && item.type == 2) show waitingLayout
            (item.status == DataConst.AppointmentStatus.APM_WAITING && item.type == 1) show btStartService
            (item.status == DataConst.AppointmentStatus.APM_IN_PROCESSING && item.type == 1) show btFinish
            (item.status == DataConst.AppointmentStatus.APM_CANCEL) show cancelLayout
            feedbackLayout.show(item.hasFeedback)
            tvFeedbackContent.text = item.feedbackContent
            ratingBar.rating = item.feedbackRating.toFloat()
            noteLayout.show(item.noteFinish.isNotEmpty())
            tvNote.text = item.noteFinish
            tvReason.text = item.reasonCancel
            totalAmountLayout.show((item.status == DataConst.AppointmentStatus.APM_FINISH))
            tvAmount.text = item.price.formatPrice()
            ratingBar.rating = item.feedbackRating.toFloat()

        }
    }

    fun updateItem(item: IAppointment) {
        val index = items?.findIndex { it.id == item.id } ?: -1
        if (index > -1) {
            items?.set(index, item)
            notifyItemChanged(index)
        }
    }

    fun removeItem(id: Int) {
        val index = items?.findIndex { it.id == id } ?: -1
        if (index > -1) {
            items?.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}