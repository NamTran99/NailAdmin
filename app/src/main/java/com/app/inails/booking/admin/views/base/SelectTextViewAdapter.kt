package com.app.inails.booking.admin.views.base

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCustomerFindingBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectTextViewAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<String, ItemCustomerFindingBinding>(view) {

    var onClickItem: ((String) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemCustomerFindingBinding {
        return parent.bindingOf(ItemCustomerFindingBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: String,
        binding: ItemCustomerFindingBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvName.text = item
            root.onClick {
                onClickItem.invoke(item)
            }
        }
    }
}