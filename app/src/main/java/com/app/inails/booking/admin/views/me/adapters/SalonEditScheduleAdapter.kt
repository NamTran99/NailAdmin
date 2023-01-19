package com.app.inails.booking.admin.views.me.adapters

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemEditSalonScheduleBinding
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import com.google.android.youtube.player.internal.i

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
            businessHour.setUpView()
            businessHour.setupListener()
            businessHour.setOnTimeChange {
                items?.set(adapterPosition, it)
//                notifyItemChanged(adapterPosition)
            }
            businessHour.setDate(item.day)
            businessHour.setTime(item.startTime, item.endTime)

        }
    }

    fun updateItem(data: List<ISchedule>?) {
        data?.forEach { newData ->
            items?.forEachIndexed { index, iSchedule ->
                if (iSchedule.day == newData.day)
                {
                    items!![index] = newData
                    notifyItemChanged(index)
                }
            }
        }

    }
}