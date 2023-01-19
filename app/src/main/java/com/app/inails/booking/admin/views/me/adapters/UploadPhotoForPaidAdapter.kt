package com.app.inails.booking.admin.views.me.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.app.inails.booking.admin.databinding.ItemPictureHolderBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions


class UploadPhotoForPaidAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<AppImage, ItemPictureHolderBinding>(view) {

    var onRemoveImageAction: ((AppImage) -> Unit)? = null
    var onItemClickListener: ((String) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemPictureHolderBinding {
        return parent.bindingOf(ItemPictureHolderBinding::inflate)
    }

    override fun onBindHolder(
        item: AppImage,
        binding: ItemPictureHolderBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            val circleProgressDrawable = CircularProgressDrawable(view.context)
            circleProgressDrawable.strokeWidth = 5f;
            circleProgressDrawable.centerRadius = 30f;
            circleProgressDrawable.start();

            val requestOptions = RequestOptions()
            requestOptions.placeholder(circleProgressDrawable)
            requestOptions.skipMemoryCache(true)
            requestOptions.fitCenter()

            Glide.with(view.context)
                .load(item.path)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgHolder)
            btClose.setOnClickListener {
                onRemoveImageAction?.invoke(item)
                removeItem(item)
            }

            imgHolder.setOnClickListener {
                onItemClickListener.invoke(item.path)
            }
        }
    }

    fun removeItem(path: AppImage) {
        val index = items?.findIndex { it == path }?: -1
        if (index > -1) {
            items?.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}

