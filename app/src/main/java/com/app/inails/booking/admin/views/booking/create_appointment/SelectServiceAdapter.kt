package com.app.inails.booking.admin.views.booking.create_appointment

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemServiceSelectBinding
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import com.app.inails.booking.admin.model.support.ISelector

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
        binding.root.apply {
            text = item.name
            configSelect(this, (item as ISelector).isSelector)
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    item.isSelector = !item.isSelector
                    notifyItemChanged(adapterPosition)
                    if (item.id == 0){
                        onClickItemListener?.invoke(item.isSelector)
                    }
                }
            }
        }
    }

    private fun configSelect(button: CheckBox, isSelected: Boolean) {
        button.apply {
            button.isChecked =isSelected
                if (isSelected) {
                button.drawableStart(R.drawable.ic_check_outline)
            } else {
                button.drawableStart()
            }
        }
    }
}