package com.app.inails.booking.admin.views.management.staff.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCheckInStaffBinding
import com.app.inails.booking.admin.databinding.ItemManageCheckInStaffBinding
import com.app.inails.booking.admin.model.ui.ICheckInOutDetail
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ManageCheckInDetailAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ICheckInOutDetail, ItemCheckInStaffBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemCheckInStaffBinding {
        return parent.bindingOf(ItemCheckInStaffBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: ICheckInOutDetail,
        binding: ItemCheckInStaffBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvDate.text = "${item.typeInOut}: ${item.dateTimeFormat}"
        }
    }
}