package com.app.inails.booking.admin.views.clients.feedbacks

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemViewFeedbackBinding
import com.app.inails.booking.admin.model.ui.client.IFeedbackItem
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class FeedbackAdapter(view: RecyclerView) :
    PageRecyclerAdapter<IFeedbackItem, ItemViewFeedbackBinding>(view) {
    var onImageSelectedListener: ((Int,List<String>) -> Unit)? = null
    override fun onCreateBinding(parent: ViewGroup): ItemViewFeedbackBinding {
        return parent.bindingOf(ItemViewFeedbackBinding::inflate)
    }

    override fun onBindHolder(
        item: IFeedbackItem,
        binding: ItemViewFeedbackBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            ratingBar.rating = item.rating
            txtTime.text = item.time
            txtName.text = item.name
            txtPhone.text = item.phone
            txtComment.text = item.contents
            val adapter = ImageFeedbackAdapter(rcvImages)
            adapter.submit(item.images)
            adapter.onImageSelectedListener = {
                onImageSelectedListener?.invoke(it,item.images?: listOf())
            }
        }
    }
}