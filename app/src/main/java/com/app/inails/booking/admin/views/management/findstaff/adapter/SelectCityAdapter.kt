package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCityFindingBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ICity
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectCityAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ICity, ItemCityFindingBinding>(view) {

    var onClickItem: ((ICity) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemCityFindingBinding {
        return parent.bindingOf(ItemCityFindingBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: ICity,
        binding: ItemCityFindingBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvCity.text = item.name
            root.onClick {
                onClickItem.invoke(item)
            }
        }
    }
}