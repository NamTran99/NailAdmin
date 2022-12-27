package com.app.inails.booking.admin.views.clients.salon

import android.graphics.Color
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemViewSalonScheduleBinding
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class SalonScheduleClientAdapter(view: RecyclerView, private val isTextWhite: Boolean = false) :
    PageRecyclerAdapter<Pair<String, String>, ItemViewSalonScheduleBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemViewSalonScheduleBinding {
        return parent.bindingOf(ItemViewSalonScheduleBinding::inflate)
    }

    override fun onBindHolder(
        item: Pair<String, String>,
        binding: ItemViewSalonScheduleBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            txtDayName.text = item.first
            txtTime.text = item.second
            txtDayName.setTextColor(if (isTextWhite) Color.WHITE else Color.BLACK)
            txtTime.setTextColor(if (isTextWhite) Color.WHITE else Color.BLACK)
        }
    }
}