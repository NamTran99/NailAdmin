package com.app.inails.booking.admin.views.clients.feedbacks

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemImageFeedbackBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.setImageUrl
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class ImageFeedbackAdapter(view: RecyclerView) :
    PageRecyclerAdapter<String, ItemImageFeedbackBinding>(view) {
    var onImageSelectedListener: ((Int) -> Unit)? = null
    override fun onCreateBinding(parent: ViewGroup): ItemImageFeedbackBinding {
        return parent.bindingOf(ItemImageFeedbackBinding::inflate)
    }

    override fun onBindHolder(
        item: String,
        binding: ItemImageFeedbackBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            imvFeedback.setImageUrl(item)
            imvFeedback.onClick{
                onImageSelectedListener?.invoke(adapterPosition)
            }
        }
    }
}