package com.app.inails.booking.admin.views.clients.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemViewServiceBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.client.IServiceClient
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ServiceAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IServiceClient, ItemViewServiceBinding>(view) {
    var onImageSelectedListener: ((List<String>) -> Unit)? = null
    val selectedItems: List<IServiceClient>
        get() = if (items.isNullOrEmpty()) emptyList()
        else items?.filter { (it as ISelector).isSelector } ?: emptyList()

    override fun onCreateBinding(parent: ViewGroup): ItemViewServiceBinding {
        return parent.bindingOf(ItemViewServiceBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun unSelectAll() {
        items?.forEach {
            (it as ISelector).isSelector = false
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IServiceClient,
        binding: ItemViewServiceBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            configSelect(this, (item as ISelector).isSelector)
            txtServiceName.text = item.name
            txtPrice.text = item.price.format()
            imvService.setImageUrl(item.images?.first() ?: "")
            root.onClick {
                item.isSelector = !item.isSelector
                notifyItemChanged(adapterPosition)
            }

            imvService.onClick {
                if (item.images?.isNotEmpty() == true) {
                    onImageSelectedListener?.invoke(item.images!!)
                }
            }
        }
    }

    private fun configSelect(binding: ItemViewServiceBinding, isSelected: Boolean) {
        with(binding) {
            txtServiceName.isSelected = isSelected
            txtPrice.isSelected = isSelected
            ivSelected.visible(isSelected)
            root.apply {
                if (isSelected) {
                    disableAlpha()
                    setBackgroundResource(R.drawable.box_white_radius_def)
                } else {
                    this.alpha(190)
                    setBackgroundResource(R.drawable.box_radius_def)
                }
            }
            if (isSelected)
                imvService.setStrokeWidthResource(R.dimen.size_1)
            else
                imvService.strokeWidth = 0f
        }
    }
}