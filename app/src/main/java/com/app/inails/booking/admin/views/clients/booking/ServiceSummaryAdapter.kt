package com.app.inails.booking.admin.views.clients.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemViewServiceSummaryBinding
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.client.IServiceClient
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ServiceSummaryAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IServiceClient, ItemViewServiceSummaryBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemViewServiceSummaryBinding {
        return parent.bindingOf(ItemViewServiceSummaryBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IServiceClient,
        binding: ItemViewServiceSummaryBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            txtServiceName.text = item.name
            txtServicePrice.text = item.price.toString()
        }
    }
}