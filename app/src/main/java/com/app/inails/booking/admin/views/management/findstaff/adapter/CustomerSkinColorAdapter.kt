package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemSkinColorBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

data class CustomerSkinColor(
    var id: Int = 0,
    @StringRes var name: Int,
    var select: Boolean = false
) {
    companion object{
        fun getList(id: Int = 0): List<CustomerSkinColor> {
            return listOf<CustomerSkinColor>(
                CustomerSkinColor(1, R.string.skin_white, id == 1),
                CustomerSkinColor(2, R.string.skin_black, id == 2),
                CustomerSkinColor(3, R.string.skin_xi, id == 3),
                CustomerSkinColor(4, R.string.skin_all, id == 4)
            )
        }
    }
}

class CustomerSkinColorAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<CustomerSkinColor, ItemSkinColorBinding>(view) {

    var onClickItem: ((CustomerSkinColor) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemSkinColorBinding {
        return parent.bindingOf(ItemSkinColorBinding::inflate)
    }

    override fun onBindHolder(
        item: CustomerSkinColor,
        binding: ItemSkinColorBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            rdItem.setText(item.name)
            rdItem.isChecked = item.select
            rdItem.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    item.select = true
                    onClickItem.invoke(item)
                    unSelectAll(item.id)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun unSelectAll(exceptId: Int) {
        items.forEach {
            if (it.id != exceptId)
                it.select = false
        }
        notifyDataSetChanged()
    }

    fun getIdSelected(): Int{
        return items.find { it.select }?.id?:-1
    }
}