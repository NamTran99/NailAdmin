package com.app.inails.booking.admin.views.management.service

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemManageServiceBinding
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

data class Service(val name: String = "")

class ManageServiceAdapter(view: RecyclerView): SimpleRecyclerAdapter<Service, ItemManageServiceBinding>(view) {
    override fun onCreateBinding(parent: ViewGroup): ItemManageServiceBinding {
        return parent.bindingOf(ItemManageServiceBinding::inflate)
    }

    override fun onBindHolder(
        item: Service,
        binding: ItemManageServiceBinding,
        adapterPosition: Int
    ) {
        binding.run {

        }
    }

}