package com.app.inails.booking.admin.views.report.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemReportListServicesBinding
import com.app.inails.booking.admin.databinding.ItemServicePriceBinding
import com.app.inails.booking.admin.extention.displaySafe
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ReportServiceAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<IService, ItemReportListServicesBinding>(view) {
    override fun onCreateBinding(parent: ViewGroup): ItemReportListServicesBinding {
        return parent.bindingOf(ItemReportListServicesBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: IService,
        binding: ItemReportListServicesBinding,
        adapterPosition: Int
    ) {
        binding.run {
            tvName.text = item.name.displaySafe()
            tvPrice.text = item.price.formatPrice()
        }
    }
}