package com.app.inails.booking.admin.views.report.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemReportSaleBinding
import com.app.inails.booking.admin.extention.displaySafe
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class ReportFragmentAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IAppointment, ItemReportSaleBinding>(view) {
    override fun onCreateBinding(parent: ViewGroup): ItemReportSaleBinding {
        return parent.bindingOf(ItemReportSaleBinding::inflate)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindHolder(
        item: IAppointment,
        binding: ItemReportSaleBinding,
        adapterPosition: Int
    ) {
        var isShow = true

        binding.run {
            tvCustomerName.text = item.customerName.displaySafe()
            tvStaffName.text = item.staffName.displaySafe()
            tvDateTime.text = item.dateTime.displaySafe()
            tvTotalAmount.text = item.totalPrice.formatPrice()

            ReportServiceAdapter(rvServices).apply {
                submit(item.serviceList)
            }

            tvActionShow.setOnClickListener {
                isShow = !isShow

                isShow.show(binding.rvServices)
                tvActionShow.text = if (isShow) "Hide" else "Show"
                val drawableID = if (isShow) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                val drawable = container.context.getDrawable(drawableID)
                tvActionShow.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            }
        }
    }
}