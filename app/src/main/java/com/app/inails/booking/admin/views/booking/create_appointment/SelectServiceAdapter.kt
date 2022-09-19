package com.app.inails.booking.admin.views.booking.create_appointment

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemServiceSelectBinding
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectServiceAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IService, ItemServiceSelectBinding>(view) {
    var onClickItemListener: ((Boolean) -> Unit)? = null
    val selectedItems: List<Int>
        get() = if (items.isNullOrEmpty()) emptyList()
        else items?.filter { (it as ISelector).isSelector }?.map { it.id } ?: emptyList()

    override fun onCreateBinding(parent: ViewGroup): ItemServiceSelectBinding {
        return parent.bindingOf(ItemServiceSelectBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IService,
        binding: ItemServiceSelectBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvServiceName.text = item.name
            tvPrice.text = item.price.formatPrice()
            configSelect(this, (item as ISelector).isSelector)
            root.onClick {
                item.isSelector = !item.isSelector
                notifyItemChanged(adapterPosition)
                if (item.id == 0) {
                    onClickItemListener?.invoke(item.isSelector)
                }
            }
        }
    }

    private fun configSelect(binding: ItemServiceSelectBinding, isSelected: Boolean) {
        binding.root.apply {
            setBackgroundResource(
                if (isSelected) {
                    R.drawable.bg_primary_radius
                } else {
                    R.drawable.bg_gray_radius
                }
            )
        }

        binding.tvServiceName.apply {
            if (isSelected) {
                drawableStart(R.drawable.ic_check_outline)
            } else {
                drawableStart()
            }
            setTextColor(
                ContextCompat.getColor(
                    context, if (isSelected) {
                        R.color.white
                    } else {
                        R.color.text_desc_color
                    }
                )
            )
        }

        binding.tvPrice.setTextColor(
            ContextCompat.getColor(
                view.context, if (isSelected) {
                    R.color.white
                } else {
                    R.color.text_desc_color
                }
            )
        )

    }
}