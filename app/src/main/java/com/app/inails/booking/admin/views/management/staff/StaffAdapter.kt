package com.app.inails.booking.admin.views.management.staff

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.databinding.ItemManageStaffBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class StaffAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IStaff, ItemManageStaffBinding>(view) {
    var onClickItemListener: ((IStaff) -> Unit)? = null
    var onUpdateStatusListener: ((Int, Int) -> Unit)? = null
    var onClickMenuListener: ((View, IStaff) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemManageStaffBinding {
        return parent.bindingOf(ItemManageStaffBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IStaff,
        binding: ItemManageStaffBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            root.onClick {
                onClickItemListener?.invoke(item)
            }

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

            (item.status == DataConst.StaffStatus.STAFF_BREAK) show btCheckIn
            (item.status == DataConst.StaffStatus.STAFF_AVAILABLE) show btCheckOut
            (item.status != DataConst.StaffStatus.STAFF_BREAK) show checkInLayout

        }
    }

    fun updateItem(staff: IStaff) {
        val index = items?.findIndex { it.id == staff.id } ?: -1
        if (index > -1) {
            items?.set(index, staff)
            notifyItemChanged(index)
        }
    }

    fun insertItem(staff: IStaff) {
        items?.add(0, staff)
        notifyItemInserted(0)

    }
}