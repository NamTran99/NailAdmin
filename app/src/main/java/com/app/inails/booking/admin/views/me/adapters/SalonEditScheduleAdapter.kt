package com.app.inails.booking.admin.views.me.adapters

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemEditSalonScheduleBinding
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SalonEditScheduleAdapter(view: RecyclerView, private val isTextWhite: Boolean = false) :
    SimpleRecyclerAdapter<ISchedule, ItemEditSalonScheduleBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemEditSalonScheduleBinding {
        return parent.bindingOf(ItemEditSalonScheduleBinding::inflate)
    }

    override fun onBindHolder(
        item: ISchedule,
        binding: ItemEditSalonScheduleBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            businessHour.setDate(item.day)
            businessHour.setStartTime(item.startTime)
            businessHour.setEndTime(item.endTime)
            businessHour.setOnTimeChange {
                items?.set(adapterPosition, it)
            }
        }
    }

     fun updateItem(data: List<ISchedule>?) {
         data?.forEach{ newData ->
             items?.forEachIndexed { index, iSchedule ->
                 if(iSchedule.day == newData.day) items!![index] = newData
             }
         }
         notifyDataSetChanged()
    }
}