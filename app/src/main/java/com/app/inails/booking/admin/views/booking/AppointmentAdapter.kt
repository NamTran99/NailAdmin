package com.app.inails.booking.admin.views.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.databinding.ItemAppointmentBinding
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.extention.formatID
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class AppointmentAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IAppointment, ItemAppointmentBinding>(view) {
    var onClickItemListener: ((IAppointment) -> Unit)? = null

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
            tvID.text = item.id.formatID()
            tvFullName.text = item.name
            tvTimeAndDate.text = item.dateAppointment
            tvServices.text = item.services
            tvRequest.text = item.staffName
            tvStatus.text = item.statusDisplay
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(view.context, item.colorStatus))
            (item.status == DataConst.AppointmentStatus.APM_WAITING) show acceptLayout
        }
    }
}