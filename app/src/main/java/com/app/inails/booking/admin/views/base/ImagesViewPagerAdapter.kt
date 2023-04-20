package com.app.inails.booking.admin.views.base

import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BasePager
import com.app.inails.booking.admin.base.PagerHolder
import com.app.inails.booking.admin.extention.getLoadingCircleDrawable
import com.app.inails.booking.admin.views.widget.ZoomImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso

class ImagesViewPagerAdapter(private val view: ViewPager) : BasePager() {
    var items: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        view.adapter = this
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) = object :
        PagerHolder<String>(container, R.layout.item_view_image) {

        override fun bind(item: String) {
            super.bind(item)
            Picasso.get().load(item)
                .placeholder(container.context.getLoadingCircleDrawable())
                .error(R.drawable.img_logo)
                .into(itemView as ZoomImageView)
        }
    }

    override fun getItem(position: Int) = items?.get(position)

    override fun getCount() = items?.size ?: 0

}