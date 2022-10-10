package com.app.inails.booking.admin.views.management.staff.adapters

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemManageCheckInStaffBinding
import com.app.inails.booking.admin.model.ui.ICheckInOutByDate
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ManageCheckInByDateAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ICheckInOutByDate, ItemManageCheckInStaffBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemManageCheckInStaffBinding {
        return parent.bindingOf(ItemManageCheckInStaffBinding::inflate)
    }

    override fun onBindHolder(
        item: ICheckInOutByDate,
        binding: ItemManageCheckInStaffBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvDate.text = item.dateFormat
            ManageCheckInDetailAdapter(rvCheckIn).apply {
                submit(item.details)
            }
        }
    }
}