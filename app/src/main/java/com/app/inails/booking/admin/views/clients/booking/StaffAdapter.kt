package com.app.inails.booking.admin.views.clients.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemViewStaffBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.client.IStaffClient
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class StaffAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IStaffClient, ItemViewStaffBinding>(view) {

    var onItemSelectedListener: (() -> Unit)? = null
    var onImageSelectedListener: ((String) -> Unit)? = null
    val selectedID: Int
        get() = if (items.isNullOrEmpty()) 0
        else {
            val result = items?.filter { (it as ISelector).isSelector } ?: listOf()
            if (result.isEmpty()) 0
            else result[0].id
        }

    val selectedItem: IStaffClient?
        get() = if (items.isNullOrEmpty()) null
        else {
            val result = items?.filter { (it as ISelector).isSelector } ?: listOf()
            if (result.isEmpty()) null
            else result[0]
        }


    override fun onCreateBinding(parent: ViewGroup): ItemViewStaffBinding {
        return parent.bindingOf(ItemViewStaffBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IStaffClient,
        binding: ItemViewStaffBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            configSelect(this, (item as ISelector).isSelector)
            txtStaffName.text = item.name
            txtStatus.setText(item.status.first)
            ivStatus.setImageResource(item.status.second)
            (item.isShowStatus) show ivStatus + txtStatus
            imvStaff.setImageUrl(item.avatar)
            root.onClick {
                unSelectAll()
                item.isSelector = !item.isSelector
                onItemSelectedListener?.invoke()
                notifyItemChanged(adapterPosition)
            }
            imvStaff.onClick{
                onImageSelectedListener?.invoke(item.avatar)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun unSelectAll() {
        items?.forEach {
            (it as ISelector).isSelector = false
        }
        notifyDataSetChanged()
    }

    private fun configSelect(binding: ItemViewStaffBinding, isSelected: Boolean) {
        with(binding) {
            txtStaffName.isSelected = isSelected
            txtStatus.isSelected = isSelected
            ivSelected.visible(isSelected)
            root.apply {
                if (isSelected) {
                    disableAlpha()
                    setBackgroundResource(R.drawable.box_white_radius_def)
                } else {
                    alpha()
                    setBackgroundResource(R.drawable.box_radius_def)
                }
            }
            if (isSelected)
                imvStaff.setStrokeWidthResource(R.dimen.size_1)
            else
                imvStaff.strokeWidth = 0f
        }
    }
}