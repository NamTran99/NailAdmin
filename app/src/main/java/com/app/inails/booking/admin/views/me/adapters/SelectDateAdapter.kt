package com.app.inails.booking.admin.views.me.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemDateSelectBinding
import com.app.inails.booking.admin.databinding.ItemServiceSelectBinding
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectDateAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ISchedule, ItemDateSelectBinding>(view) {
    val selectedItems: List<Int>
        get() = if (items.isNullOrEmpty()) emptyList()
        else items?.filter { (it as ISelector).isSelector }?.map { it.day }
            ?: emptyList()

    override fun onCreateBinding(parent: ViewGroup): ItemDateSelectBinding {
        return parent.bindingOf(ItemDateSelectBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: ISchedule,
        binding: ItemDateSelectBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvDate.setText(item.dayFormat)
            configSelect(this, (item as ISelector).isSelector)
            imgTick.onClick {
                item.isSelector = !item.isSelector
                notifyItemChanged(adapterPosition)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun configSelect(binding: ItemDateSelectBinding, isSelected: Boolean) {
        binding.imgTick.apply {
            setImageDrawable(
                context.getDrawable(
                    if (isSelected) {
                        R.drawable.ic_date_tick
                    } else {
                        R.drawable.ic_date_uncheck
                    }
                )
            )
        }
    }

//    fun setSelected(list: List<ISchedule>) {
//        list.forEach {
//            items?.forEachIndexed { index, iService ->
//                if (it.id == iService.id) {
//                    (items!![index] as ISelector).isSelector = true
//                }
//            }
//        }
//        notifyDataSetChanged()
//    }
}