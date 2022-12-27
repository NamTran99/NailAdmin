package com.app.inails.booking.admin.views.clients.feedbacks

import android.net.Uri
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemImageFeedbackBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.setImageUri
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class AddImageFeedbackAdapter(view: RecyclerView) :
    PageRecyclerAdapter<Uri, ItemImageFeedbackBinding>(view) {
    var onImageSelectedListener: ((Int) -> Unit)? = null
    var onRemoveListener: ((Uri) -> Unit)? = null
    override fun onCreateBinding(parent: ViewGroup): ItemImageFeedbackBinding {
        return parent.bindingOf(ItemImageFeedbackBinding::inflate)
    }

    override fun onBindHolder(
        item: Uri,
        binding: ItemImageFeedbackBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            if (adapterPosition == 0) {
                addLayout.show()
                imvFeedback.hide()
                btRemove.hide()
            } else {
                addLayout.hide()
                imvFeedback.show()
                btRemove.show()
                imvFeedback.setImageUri(item)
            }
            addLayout.onClick {
                onImageSelectedListener?.invoke(adapterPosition)
            }

            btRemove.onClick {
                onRemoveListener?.invoke(item)
                items().getData().removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    fun getImageSelected(): MutableList<Uri> =
        items().getData().filterIndexed { index, uri -> (index != 0) }.toMutableList()
}