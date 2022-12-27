package com.app.inails.booking.admin.views.management.service.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemServiceImagesBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter


class AppImagesAdapter(view: RecyclerView) :
    PageRecyclerAdapter<AppImage, ItemServiceImagesBinding>(view) {

    var onItemImageClick: ((Int) -> Unit) ={}

    override fun onCreateBinding(parent: ViewGroup): ItemServiceImagesBinding {
        return parent.bindingOf(ItemServiceImagesBinding::inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHolder(
        item: AppImage,
        binding: ItemServiceImagesBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            image.setImageUrl(item.path)
            root.onClick{
                onItemImageClick.invoke(adapterPosition)
            }
        }
    }
}