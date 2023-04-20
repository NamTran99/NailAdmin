package com.app.inails.booking.admin.views.booking.create_appointment.adapter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCustomerFindingBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import kotlinx.coroutines.NonDisposableHandle.parent

class SelectCustomerAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ICustomer, ItemCustomerFindingBinding>(view) {
    val selectedItems: List<Int>
        get() = if (items.isNullOrEmpty()) emptyList()
        else items?.filter { (it as ISelector).isSelector && it.id != 0 }?.map { it.id }
            ?: emptyList()

    var onClickItem : ((ICustomer) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemCustomerFindingBinding {
        return parent.bindingOf(ItemCustomerFindingBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: ICustomer,
        binding: ItemCustomerFindingBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvName.text = item.name
            tvPhone.text = item.phone
            root.onClick{
                onClickItem.invoke(item)
            }
        }
    }
}