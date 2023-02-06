package com.app.inails.booking.admin.views.me.adapters

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemEditSalonScheduleBinding
import com.app.inails.booking.admin.databinding.ItemVoucherBinding
import com.app.inails.booking.admin.extention.setTextViewDrawableColor
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.model.ui.IVoucher
import com.app.inails.booking.admin.model.ui.VoucherType
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import kotlinx.coroutines.NonDisposableHandle.parent

class VoucherAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IVoucher, ItemVoucherBinding>(view) {

    var onItemCLick: ((IVoucher) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemVoucherBinding {
        return parent.bindingOf(ItemVoucherBinding::inflate)
    }

    override fun onBindHolder(
        item: IVoucher,
        binding: ItemVoucherBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvCode.text = item.code
            root.setOnClickListener {
                onItemCLick.invoke(item)
            }

            tvStatus.setText(item.statusName)
            tvStatus.setTextColor(root.context.getColor(item.statusColor))
//            tvCustomerType.text = item.typeCustomer
//            if(item.type == VoucherType.PERCENT){
//                tvValue.text = "-${item.value}%"
//            }else{
//                tvValue.text = "-${item.value}$"
//            }
//            tvStartTime.text = item.startDate
//            tvEndTime.text = item.endDate
        }
    }
}