package com.app.inails.booking.admin.views.booking.create_appointment

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemStaffSelectBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectStaffAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IStaff, ItemStaffSelectBinding>(view) {
    var onClickItemListener: ((IStaff) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemStaffSelectBinding {
        return parent.bindingOf(ItemStaffSelectBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IStaff,
        binding: ItemStaffSelectBinding,
        adapterPosition: Int
    ) {
        binding.apply{
            root.onClick{
                onClickItemListener?.invoke(item)
            }
            btAssign.setOnClickListener {
                onClickItemListener?.invoke(item)
            }
            tvPhone.text = item.phone
            tvStaffName.text = item.name
        }
    }
}