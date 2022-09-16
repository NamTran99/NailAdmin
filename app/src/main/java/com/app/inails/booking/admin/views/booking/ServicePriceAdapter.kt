package com.app.inails.booking.admin.views.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemServicePriceBinding
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ServicePriceAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IService, ItemServicePriceBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemServicePriceBinding {
        return parent.bindingOf(ItemServicePriceBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IService,
        binding: ItemServicePriceBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvServiceName.text = item.name
            tvPrice.text = item.price.formatPrice()
        }
    }
}