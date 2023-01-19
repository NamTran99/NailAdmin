package com.app.inails.booking.admin.views.main

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.databinding.ItemStaffStatusBinding
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class StaffStatusAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IStaff, ItemStaffStatusBinding>(view) {
    var onClickAppointmentListener: ((IAppointment?) -> Unit)? = null
    var onClickShowStaffInfor: ((IStaff?) -> Unit)? = null
    var onClickShowCustomerInfor: ((ICustomer?) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemStaffStatusBinding {
        return parent.bindingOf(ItemStaffStatusBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IStaff,
        binding: ItemStaffStatusBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvAppointmentID.setOnClickListener {
                onClickAppointmentListener?.invoke(item.appointment)
            }
            tvStaffName.onClick{
                onClickShowStaffInfor?.invoke(item)
            }
            tvCustomerName.onClick{
                onClickShowCustomerInfor?.invoke(item.appointment?.customer)
            }
            tvStaffName.text = item.name
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.text = item.statusName
            tvStatus.setTextColor(ContextCompat.getColor(view.context, item.colorStatus))
            if (item.status == DataConst.StaffStatus.STAFF_WORKING) {
                tvCustomerName.text = item.customerName
                tvAppointmentID.text = "#${item.appointment?.id}"
                tvDateTime.text = "(${item.timeCheckInAppointment} - ${item.timeEndAppointment})"
            } else {
                tvDateTime.hide()
                tvAppointmentID.hide()
                tvCustomerName.hide()
            }
        }
    }
}