package com.app.inails.booking.admin.views.clients.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemViewTimeBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.client.ITime
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import com.google.android.material.button.MaterialButton

class TimeAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ITime, ItemViewTimeBinding>(view) {

    val timeSelected: ITime?
        get() = if (items.isNullOrEmpty()) null
        else {
            val results = items?.filter { (it as ISelector).isSelector } ?: listOf()
            if (results.isNotEmpty())
                results[0]
            else null
        }

    var onItemClickListener: ((ITime) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemViewTimeBinding {
        return parent.bindingOf(ItemViewTimeBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: ITime,
        binding: ItemViewTimeBinding,
        adapterPosition: Int
    ) {
        binding.root.apply {
            text = item.timeDisplay
            configSelect(this, (item as ISelector).isSelector)
            onClick {
                unSelectAll()
                item.isSelector = !item.isSelector
                notifyItemChanged(adapterPosition)
                onItemClickListener?.invoke(item)
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

    private fun configSelect(button: MaterialButton, isSelected: Boolean) {
        button.apply {
            if (isSelected) {
                disableAlpha()
                backgroundTintList = context.colorStateList(R.color.white)
                setIconResource(R.drawable.ic_check)
                setIconTintResource(R.color.colorAccent)
                setTextColor(context.colorResource(R.color.colorAccent))
            } else {
                alpha(190)
                backgroundTintList = context.colorStateList(R.color.colorAccent)
                setIconResource(0)
                setTextColor(context.colorResource(R.color.white))
            }
        }
    }
}