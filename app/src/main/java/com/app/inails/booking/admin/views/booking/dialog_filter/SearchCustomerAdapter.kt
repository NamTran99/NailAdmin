package com.app.inails.booking.admin.views.booking.dialog_filter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCustomerSearchBinding
import com.app.inails.booking.admin.extention.formatPhoneUSCustom
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter


class SearchCustomerAdapter(view: RecyclerView) :
    PageRecyclerAdapter<ICustomer, ItemCustomerSearchBinding>(view) {

    var onClickItemListener: ((ICustomer) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemCustomerSearchBinding {
        return parent.bindingOf(ItemCustomerSearchBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: ICustomer,
        binding: ItemCustomerSearchBinding,
        adapterPosition: Int
    ) {
        binding.run {
            root.onClick {
                onClickItemListener?.invoke(item)
            }
            tvCustomerName.text = item.name
            tvPhone.text = item.phone.formatPhoneUSCustom()
        }
    }
}