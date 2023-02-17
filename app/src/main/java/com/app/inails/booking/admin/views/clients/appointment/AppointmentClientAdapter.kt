package com.app.inails.booking.admin.views.clients.appointment

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.databinding.adapters.TextViewBindingAdapter.setDrawableStart
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemViewAppointmentBinding
import com.app.inails.booking.admin.databinding.ItemViewAppointmentClientBinding
import com.app.inails.booking.admin.extention.colorResource
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.setDrawableStart
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.client.IAppointmentClient
import com.app.inails.booking.admin.model.ui.client.StatusEditable
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class AppointmentClientAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IAppointmentClient, ItemViewAppointmentClientBinding>(view) {

    var onItemClickListener: ((Pair<Long,Long>) -> Unit)? = null
    var onCancelClickListener: ((Pair<IAppointmentClient, Int>) -> Unit)? = null
    var onFeedbackClickListener: ((Pair<IAppointmentClient, Int>) -> Unit)? = null
    var onDeleteClickListener: ((Pair<IAppointmentClient, Int>) -> Unit)? = null
    var onDirectSalonClickListener: ((Pair<Float, Float>) -> Unit)? = null
    var onSalonClickListener: ((Long) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemViewAppointmentClientBinding {
        return parent.bindingOf(ItemViewAppointmentClientBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged", "RestrictedApi")
    override fun onBindHolder(
        item: IAppointmentClient,
        binding: ItemViewAppointmentClientBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            txtDatetime.text = item.dateTime
            txtService.text = item.serviceDisplay
            txtRequest.text = item.staffName
            txtBookingID.text = item.idDisplay
            txtSalonName.text = item.salonName
            txtCancelBy.text = item.canceledBy
            txtStatus.apply {
                setText(item.status.first)
                setTextColor(context.colorResource(item.status.second))
                setDrawableStart(item.status.third)
            }
            btnCancel.show(item.isShowCancelButton)
            btnCancel.onClick { onCancelClickListener?.invoke(item to adapterPosition) }
            cancelLayout.show(item.isShowCancelNote)
            txtReason.text = item.reasonCancel
            totalAmountLayout.show(item.isFinish)
            noteLayout.show(item.isShowNote)
            txtAmount.text = item.totalAmount
            txtNote.text = item.finishNote
            finishLayout.show(item.isFinish)
            btnFeedback.show(!item.hasFeedBack)
            btnDirect.show(item.isShowDirect)
            btnFeedback.onClick { onFeedbackClickListener?.invoke(item to adapterPosition) }
            feedbackLayout.show(item.hasFeedBack)
            txtFeedbackContent.text = item.feedbackContent
            ratingBar.rating = item.feedbackRating.toFloat()
            txtCreateAt.text = item.createAtDisplay
            txtSomethingElse.text = item.note
            btnDelete.onClick { onDeleteClickListener?.invoke(item to adapterPosition) }
            txtSalonName.onClick { onSalonClickListener?.invoke(item.salonID) }
            btnDirect.onClick { onDirectSalonClickListener?.invoke(item.lat to item.lng) }
            itemView.onClick {
                onItemClickListener?.invoke(item.id to item.salonID) }
        }
    }

    fun notifyStatusCancel(it: Pair<Int, String>) {//position/reason
        val position = it.first
        if (items?.get(position) is StatusEditable) {
            (items?.get(position) as StatusEditable).setCancelStatus(it.second)
            notifyItemChanged(position)
        }
    }

    fun notifyRemoved(it: Int) {
        if (items.isNullOrEmpty()) return
        (items as MutableList<*>).removeAt(it)
        notifyItemRemoved(it)
    }
}