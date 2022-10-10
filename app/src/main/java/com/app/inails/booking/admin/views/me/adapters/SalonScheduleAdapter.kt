package com.app.inails.booking.admin.views.me.adapters

import android.graphics.Color
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemViewSalonScheduleBinding
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class SalonScheduleAdapter(view: RecyclerView, private val isTextWhite: Boolean = false) :
    PageRecyclerAdapter<ISchedule, ItemViewSalonScheduleBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemViewSalonScheduleBinding {
        return parent.bindingOf(ItemViewSalonScheduleBinding::inflate)
    }

    override fun onBindHolder(
        item: ISchedule,
        binding: ItemViewSalonScheduleBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            txtDayName.text = item.dayFormat
            txtTime.text = item.timeFormat
            txtDayName.setTextColor(if (isTextWhite) Color.WHITE else Color.BLACK)
            txtTime.setTextColor(if (isTextWhite) Color.WHITE else Color.BLACK)
        }
    }

    override fun submit(items: List<ISchedule>?) {
        clear()
        super.submit(items)
    }
}