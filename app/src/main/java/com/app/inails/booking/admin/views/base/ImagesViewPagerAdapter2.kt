package com.app.inails.booking.admin.views.base

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.databinding.ItemViewPagerImageBinding
import com.app.inails.booking.admin.extention.getLoadingCircleDrawable
import com.app.inails.booking.admin.views.me.adapters.LoopHandler
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter2
import com.squareup.picasso.Picasso

class ImagesViewPagerAdapter2(recyclerView: ViewPager2) :
    SimpleRecyclerAdapter2<String, ItemViewPagerImageBinding>(recyclerView) {

    var onItemClick: ((position:Int) -> Unit) = {}

    private val mViewLooper = LoopHandler {
        val next = (view.currentItem + 1) % itemCount
        view.setCurrentItem(next, true)
    }

    override fun onCreateBinding(parent: ViewGroup): ItemViewPagerImageBinding {
        return parent.bindingOf(ItemViewPagerImageBinding::inflate)
    }

    override fun onBindHolder(
        item: String,
        binding: ItemViewPagerImageBinding,
        adapterPosition: Int
    ) {
        val context = binding.root.context
        binding.apply {
            root.setOnClickListener {
                onItemClick.invoke(adapterPosition)
            }
            Picasso.get().load(item)
                .centerInside()
                .resize(2048, 2048)
                .placeholder(context.getLoadingCircleDrawable()).into(
                    image
                )
        }
    }

    override fun submit(newItems: List<String>?) {
        super.submit(newItems)
        if (itemCount > 0) mViewLooper.start()
        else mViewLooper.stop()
    }
}

