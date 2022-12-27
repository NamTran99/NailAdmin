package com.app.inails.booking.admin.views.me.adapters

import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemGalleryImageBinding
import com.app.inails.booking.admin.databinding.ItemViewImageViewerBinding
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.views.extension.LocalImage
import com.app.inails.booking.admin.views.widget.PinchZoomImageView
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter2

class ImagePagerAdapter(view: ViewPager2) :
    SimpleRecyclerAdapter2<AppImage, ItemViewImageViewerBinding>(view) {

    override fun onCreateBinding(parent: ViewGroup): ItemViewImageViewerBinding {
        return parent.bindingOf(ItemViewImageViewerBinding::inflate)
    }

    private fun loadImage(imageView: PinchZoomImageView, loading: ProgressBar, image: AppImage) {
        showLoading(imageView, loading)
        imageView.setImageUrl(image.path) { showImage(imageView, loading) }
    }

    private fun showImage(imageView: ImageView, loading: ProgressBar) {
        imageView.visibility = View.VISIBLE
        loading.visibility = View.GONE
    }

    private fun showLoading(imageView: ImageView, loading: ProgressBar) {
        imageView.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    override fun onBindHolder(
        item: AppImage,
        binding: ItemViewImageViewerBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            loadImage(imgZoom, progressBar, item)
        }
    }
}