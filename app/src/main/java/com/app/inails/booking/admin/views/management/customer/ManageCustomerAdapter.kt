package com.app.inails.booking.admin.views.management.customer

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemManageCustomerBinding
import com.app.inails.booking.admin.databinding.ItemManageServiceBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter


class ManageCustomerAdapter(view: RecyclerView) :
    PageRecyclerAdapter<ICustomer, ItemManageCustomerBinding>(view) {

    var onClickItemListener: ((ICustomer) -> Unit)? = null
    var onClickMenuListener: ((View, ICustomer) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemManageCustomerBinding {
        return parent.bindingOf(ItemManageCustomerBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: ICustomer,
        binding: ItemManageCustomerBinding,
        adapterPosition: Int
    ) {
        binding.run {
            // init view
            tvAddress.text = item.address
            tvCustomerName.text = item.name
            tvEmail.text = item.email
            tvPhone.text = item.phone
        }
    }



}