package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemJobProfileBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IJobProfile
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class JobProfileAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IJobProfile, ItemJobProfileBinding>(view) {
    override fun onCreateBinding(parent: ViewGroup): ItemJobProfileBinding {
        return parent.bindingOf(ItemJobProfileBinding::inflate)
    }

    var onClickMenuListener: ((View, IJobProfile) -> Unit)? = null
    var onItemClick: ((IJobProfile) -> Unit) = {}

    fun removeItem(id: Int) {
        val index = getData().findIndex { it.id == id } ?: -1
        if (index > -1) {
            getData().removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateItem(item: IJobProfile) {
        val index = items().getData().findIndex { it.id == item.id } ?: -1
        if (index > -1) {
            items().getData()[index] = item
            notifyItemChanged(index)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: IJobProfile,
        binding: ItemJobProfileBinding,
        adapterPosition: Int
    ) {
        with(binding) {
            root.onClick {
                onItemClick.invoke(item)
            }
            imgBanner.setImageUrl(item.avatar)
            tvName.text = item.name
            tvGender.text = item.genderName
            tvExperience.text = item.experienceFormat
            tvWorkOutState.text = item.workplace_type_format
            tvWorkplace.text = item.workingArea
        }
    }
}