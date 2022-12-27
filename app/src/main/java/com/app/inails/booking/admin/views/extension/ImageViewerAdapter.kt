package com.app.inails.booking.admin.views.extension

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BasePager
import com.app.inails.booking.admin.base.PagerHolder
import com.app.inails.booking.admin.views.widget.PinchZoomImageView


class ImageViewerAdapter// constructor
    (private val view: ViewPager) : BasePager() {

    var items: List<LocalImage>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        view.adapter = this
    }

    override fun getCount() = items?.size ?: 0

    override fun getItem(position: Int) = items?.get(position)


//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view === `object`
//    }

    private fun loadImage(imageView: PinchZoomImageView, loading: ProgressBar, image: LocalImage) {
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

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) =
        object : PagerHolder<LocalImage>(container, R.layout.item_view_image_viewer) {
            override fun bind(item: LocalImage) {
                super.bind(item)

                val mImageView: PinchZoomImageView = findViewById(R.id.imgZoom)
                val mLoading: ProgressBar = findViewById(R.id.progressBar)

                loadImage(mImageView, mLoading, item)
            }
        }
}
