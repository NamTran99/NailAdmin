package com.app.inails.booking.admin.views.me.adapters

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.databinding.adapters.CalendarViewBindingAdapter.setDate
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemEditSalonScheduleBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import com.google.android.youtube.player.internal.i

class SalonEditScheduleAdapter(view: RecyclerView, private val isTextWhite: Boolean = false) :
    SimpleRecyclerAdapter<ISchedule, ItemEditSalonScheduleBinding>(view) {

    var onItemCopyClick: (ISchedule)-> Unit = {}

    override fun onCreateBinding(parent: ViewGroup): ItemEditSalonScheduleBinding {
        return parent.bindingOf(ItemEditSalonScheduleBinding::inflate)
    }

    override fun onBindHolder(
        item: ISchedule,
        binding: ItemEditSalonScheduleBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            businessHour.apply {
                setUpView()
                setupListener()
//                isOpenListener = {
//                    btCopy.show(it)
//                }
                setOnTimeChange {
                    items?.set(adapterPosition, it)
                    btCopy.show(it.startTime != null && it.endTime != null)
                }
                setDate(item.day)
                setTime(item.startTime, item.endTime)
            }
            btCopy.onClick{
                onItemCopyClick.invoke(items!![adapterPosition])
            }
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