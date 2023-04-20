package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCustomerFindingBinding
import com.app.inails.booking.admin.databinding.ItemStateFindingBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IState
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectStateAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IState, ItemStateFindingBinding>(view) {

    var onClickItem: ((IState) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemStateFindingBinding {
        return parent.bindingOf(ItemStateFindingBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IState,
        binding: ItemStateFindingBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvState.text = item.fullName
            tvAcronym.text = item.name
            root.onClick {
                onClickItem.invoke(item)
            }
        }
    }
}