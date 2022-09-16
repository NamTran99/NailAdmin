package com.app.inails.booking.admin.views.main

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.databinding.ItemStaffStatusBinding
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class StaffStatusAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IStaff, ItemStaffStatusBinding>(view) {
    var onClickItemListener: ((IStaff) -> Unit)? = null

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
            root.onClick {
                onClickItemListener?.invoke(item)
            }
            tvStaffName.text = item.name
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(view.context, item.colorStatus))
            if (item.status == DataConst.StaffStatus.STAFF_WORKING){
                tvCustomerName.text = "${item.customerName} (${item.timeCheckInAppointment})"
                tvStatus.text = "${item.statusName} (${item.timeCheckIn})"
            }else{
                tvCustomerName.text =""
                tvStatus.text = item.statusName
            }

        }
    }
}