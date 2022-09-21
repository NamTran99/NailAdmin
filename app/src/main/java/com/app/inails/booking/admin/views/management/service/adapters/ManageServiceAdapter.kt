package com.app.inails.booking.admin.views.management.service.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemManageServiceBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter


class ManageServiceAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IService, ItemManageServiceBinding>(view) {

    var onClickMenuListener: ((View, IService) -> Unit)? = null
    var onClickItemListener: ((IService) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemManageServiceBinding {
        return parent.bindingOf(ItemManageServiceBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: IService,
        binding: ItemManageServiceBinding,
        adapterPosition: Int
    ) {
        binding.run {
            root.setOnClickListener {
                onClickItemListener?.invoke(item)
            }

            btMenu.setOnClickListener {
                onClickMenuListener?.invoke(it, item)
            }

            tvName.text = item.name
            tvPrice.text = "${item.price.formatPrice()}"

            tvPrice.setTextColor(ContextCompat.getColor(view.context, item.textColor))
            tvName.setTextColor(ContextCompat.getColor(view.context, item.textColor))
        }
    }

    fun updateItem(service: IService) {
        val index = getData().findIndex { it.id == service.id }
        if (index > -1) {
            getData().set(index, service)
            notifyItemChanged(index)
        }
    }

    fun insertItem(service: IService) {
        getData().add(0, service)
        notifyItemInserted(0)
    }

    fun removeItem(id: Int) {
        val index = getData().findIndex { it.id == id }
        if (index > -1) {
            getData().removeAt(index)
            notifyItemRemoved(index)
        }
    }

}