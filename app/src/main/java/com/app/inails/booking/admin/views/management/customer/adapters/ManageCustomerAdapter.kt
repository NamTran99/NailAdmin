package com.app.inails.booking.admin.views.management.customer.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemManageCustomerBinding
import com.app.inails.booking.admin.extention.displaySafe
import com.app.inails.booking.admin.extention.formatPhoneUS
import com.app.inails.booking.admin.extention.formatPhoneUSCustom
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter


class ManageCustomerAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ICustomer, ItemManageCustomerBinding>(view) {

    var onClickItemListener: ((ICustomer) -> Unit)? = null
    var onClickOpenBookingList: ((ICustomer) -> Unit)? = null

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
            tvAddress.text = item.address.displaySafe()
            tvCustomerName.text = item.name.displaySafe()
            tvEmail.text = item.email.displaySafe()
            tvPhone.text = item.phone.formatPhoneUSCustom().displaySafe()

            btBookingList.setOnClickListener {
                onClickOpenBookingList?.invoke(item)
            }
        }
    }
}