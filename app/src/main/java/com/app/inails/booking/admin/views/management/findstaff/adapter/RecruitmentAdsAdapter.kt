package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.os.Build.VERSION_CODES.P
import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemRecruitmentBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IRecruitment
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.squareup.picasso.Picasso

class RecruitmentAdsAdapter(view: RecyclerView, val isOwnerMode: Boolean = true) :
    PageRecyclerAdapter<IRecruitment, ItemRecruitmentBinding>(view) {
    override fun onCreateBinding(parent: ViewGroup): ItemRecruitmentBinding {
        return parent.bindingOf(ItemRecruitmentBinding::inflate)
    }

    var onClickMenuListener: ((View, IRecruitment) -> Unit)? = null
    var onItemClick: ((IRecruitment) -> Unit) = {}

    fun removeItem(id: Int) {
        val index = getData().findIndex { it.id == id } ?: -1
        if (index > -1) {
            getData().removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateItem(item: IRecruitment) {
        val index = items().getData().findIndex { it.id == item.id } ?: -1
        if (index > -1) {
            items().getData()[index] = item
            notifyItemChanged(index)
        }
    }

    override fun onBindHolder(
        item: IRecruitment,
        binding: ItemRecruitmentBinding,
        adapterPosition: Int
    ) {
        val context = binding.root.context
        with(binding) {
            root.onClick{
                onItemClick.invoke(item)
            }
            tvDate.text = item.create_at
            if(item.avatar.isNotEmpty()){
                Picasso.get()
                    .load(item.avatar)
                    .resize(2048,2048)
                    .centerInside()
                    .placeholder(context.getLoadingCircleDrawable())
                    .into(imgBanner)
            }else{
                Picasso.get()
                    .load(R.drawable.img_logo)
                    .resize(2048,2048)
                    .centerInside()
                    .placeholder(context.getLoadingCircleDrawable())
                    .into(imgBanner)
            }
            btMenu.visible(isOwnerMode)
            btMenu.onClick{
                onClickMenuListener?.invoke(it, item)
            }
            tvStatusPub.show(item.isPublish)
            tvStatusUnPub.show(!item.isPublish)
            tvTitle.text= item.title
            tvWorkplace.text = item.work_place
            tvIncome.text = item.averageIncomeFormat
            tvPayrollMethod.text = item.payrollMethodFormat
        }
    }
}