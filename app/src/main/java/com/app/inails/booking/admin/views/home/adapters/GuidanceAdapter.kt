package com.app.inails.booking.admin.views.home.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemGuidanceSliderBinding
import com.app.inails.booking.admin.extention.setImageURICustom
import com.app.inails.booking.admin.model.ui.FileType
import com.app.inails.booking.admin.model.ui.IIntro
import com.app.inails.booking.admin.views.me.adapters.LoopHandler
import com.app.inails.booking.admin.views.widget.SimpleViewPagerAdapter
import kotlinx.coroutines.NonCancellable.start

class GuidanceAdapter(recyclerView: ViewPager2) :
    SimpleViewPagerAdapter<IIntro, ItemGuidanceSliderBinding>(recyclerView) {

    var onItemClick: ((IIntro) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemGuidanceSliderBinding {
        return parent.bindingOf(ItemGuidanceSliderBinding::inflate)
    }

    private val mViewLooper = LoopHandler {
        val next = (view.currentItem + 1) % itemCount
        view.setCurrentItem(next, true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun submit(newItems: List<IIntro>?) {
        super.submit(newItems)
        view.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mViewLooper.start()
                } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mViewLooper.stop()
                }
            }
        })
        if (itemCount > 0) mViewLooper.start()
        else mViewLooper.stop()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindHolder(
        item: IIntro,
        binding: ItemGuidanceSliderBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            val context = root.context
//            imgYoutube.show(item.fileType == FileType.Video)
            root.setOnClickListener {
                onItemClick.invoke(item)
            }
            if (item.fileType == FileType.Video) {
                if (item.thumbNail == "") {
                    image.setImageDrawable(context.getDrawable(R.drawable.ic_default_guidance))
                } else {
                    image.setImageURICustom(item.thumbNail)
                }
            } else {
//                image.setImageDrawable(context.getDrawable(R.drawable.ic_default_guidance))
                if (item.file.isEmpty()) return
                image.setImageURICustom(item.file)
            }

        }
    }
}
