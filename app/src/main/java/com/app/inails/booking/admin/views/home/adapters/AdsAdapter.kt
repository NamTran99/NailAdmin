package com.app.inails.booking.admin.views.home.adapters

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.databinding.ItemAdsSliderBinding
import com.app.inails.booking.admin.extention.setImageURICustom
import com.app.inails.booking.admin.model.ui.IIntro
import com.app.inails.booking.admin.views.me.adapters.LoopHandler
import com.app.inails.booking.admin.views.widget.SimpleViewPagerAdapter

class AdsAdapter(recyclerView: ViewPager2) :
    SimpleViewPagerAdapter<IIntro, ItemAdsSliderBinding>(recyclerView) {

    var onItemClick :((IIntro) -> Unit) = {}

    private val mViewLooper = LoopHandler {
        val next = (view.currentItem + 1) % itemCount
        view.setCurrentItem(next, true)
    }

    override fun onCreateBinding(parent: ViewGroup): ItemAdsSliderBinding {
       return parent.bindingOf(ItemAdsSliderBinding::inflate)
    }

    override fun onBindHolder(item: IIntro, binding: ItemAdsSliderBinding, adapterPosition: Int) {
        binding.apply {
            root.setOnClickListener {
                onItemClick.invoke(item)
            }
            image.setImageURICustom(item.image)
        }
    }

    override fun submit(newItems: List<IIntro>?) {
        super.submit(newItems)
        if (itemCount > 0) mViewLooper.start()
        else mViewLooper.stop()
    }
}

