package com.app.inails.booking.admin.views.booking.dialog_filter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemCustomerSearchBinding
import com.app.inails.booking.admin.databinding.ItemStaffSearchBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter


class SearchStaffAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IStaff, ItemStaffSearchBinding>(view) {

    var onClickItemListener: ((IStaff) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemStaffSearchBinding {
        return parent.bindingOf(ItemStaffSearchBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: IStaff,
        binding: ItemStaffSearchBinding,
        adapterPosition: Int
    ) {
        binding.run {
            root.onClick {
                onClickItemListener?.invoke(item)
            }
            tvStaffName.text = item.name
            tvPhone.text = item.phone
        }
    }
}