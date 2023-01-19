package com.app.inails.booking.admin.views.management.staff.adapters

import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemManageStaffBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ManageStaffAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IStaff, ItemManageStaffBinding>(view) {
    var onClickAvatarImage: ((String?) -> Unit)? = null
    var onUpdateStatusListener: ((Int, Int) -> Unit)? = null
    var onClickMenuListener: ((View, IStaff) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemManageStaffBinding {
        return parent.bindingOf(ItemManageStaffBinding::inflate)
    }

    override fun onBindHolder(item: IStaff, binding: ItemManageStaffBinding, adapterPosition: Int) {
        binding.apply {
            imgImage.onClick{
                onClickAvatarImage?.invoke(item.avatar)
            }
                imgImage.setImageUrl(item.avatar)
            tvDescription.show(
                 item.description.isNotEmpty()
            )
            tvDescription.text = item.description

            btCheckIn.setOnClickListener {
                onUpdateStatusListener?.invoke(item.id, DataConst.ChangeStaffStatus.CHECK_IN)
            }

            btCheckOut.setOnClickListener {
                onUpdateStatusListener?.invoke(item.id, DataConst.ChangeStaffStatus.CHECK_OUT)
            }

            btMenu.setOnClickListener {
                onClickMenuListener?.invoke(it, item)
            }

            tvPhone.text = item.phone
            tvStaffName.text = item.name
            tvTimeCheckedIn.text = item.timeCheckIn

            (item.status == DataConst.StaffStatus.STAFF_BREAK && item.active == 1) show btCheckIn
            (item.status != DataConst.StaffStatus.STAFF_BREAK && item.active == 1) show checkOutLayout
            tvStaffName.setTextColor(ContextCompat.getColor(view.context, item.textColor))
            tvPhone.setTextColor(ContextCompat.getColor(view.context, item.textColor))
            tvPhone.setLinkTextColor(ContextCompat.getColor(view.context, item.textColor))

        }
    }

    fun updateItem(staff: IStaff) {
        val index = getData().findIndex { it.id == staff.id }
        if (index > -1) {
            getData()[index] = staff
            notifyItemChanged(index)
        }
    }

    fun insertItem(staff: IStaff) {
        getData().add(0, staff)
        notifyItemInserted(0)
    }

    fun removeItem(id: Int) {
        val index = getData().findIndex { it.id == id }
        if (index > -1) {
            getData().removeAt(index)
            notifyItemRemoved(index)
        }
    }
}