package com.app.inails.booking.admin.views.management.customer.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCustomerBookingListBinding
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter


class ManageCustomerListBookingAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IService, ItemCustomerBookingListBinding>(view) {

    var onClickItemListener: ((IService) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemCustomerBookingListBinding {
        return parent.bindingOf(ItemCustomerBookingListBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: IService,
        binding: ItemCustomerBookingListBinding,
        adapterPosition: Int
    ) {
        binding.run {
            tvID.text = "ID: #${item.id}"
            tvServiceName.text = item.name
            tvTotalAmount.text = item.price.formatPrice()
        }
    }
}