package com.app.inails.booking.admin.views.management.customer.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemManageCustomerBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter


class ManageCustomerAdapter(view: RecyclerView) :
    PageRecyclerAdapter<ICustomer, ItemManageCustomerBinding>(view) {

    var onClickItemListener: ((ICustomer) -> Unit)? = null
    var onClickCreateAppointment: ((ICustomer) -> Unit) = {}
    var onClickMenuListener: ((View, ICustomer) -> Unit)? = null

    override fun onCreateBinding(parent: ViewGroup): ItemManageCustomerBinding {
        return parent.bindingOf(ItemManageCustomerBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: ICustomer,
        binding: ItemManageCustomerBinding,
        adapterPosition: Int
    ) {
        binding.run {
            btCreateAppointment.onClick{
                onClickCreateAppointment.invoke(item)
            }
            lvVip.visible(item.type == 3)
            lvNote.show(item.isShowNote)
            tvNote.text = item.note
            // init view
            tvAddress.text = item.address
            tvCustomerName.text = item.name
            tvEmail.text = item.email
            tvPhone.text = item.phone.formatPhoneUSCustom()
            tvBirthday.text = item.birthDay

            btMenu.setOnClickListener {
                onClickMenuListener?.invoke(it, item)
            }

            btCreateAppointment.setOnClickListener {
                onClickCreateAppointment?.invoke(item)
            }
        }
    }

    fun updateItem(item: ICustomer) {
        val index = items().getData().findIndex { it.id == item.id } ?: -1
        if (index > -1) {
            items().getData()[index] = item
            notifyItemChanged(index)
        }
    }
}