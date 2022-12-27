package com.app.inails.booking.admin.views.me.adapters

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.databinding.ItemGalleryImageBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter2

class GalleryImagePagerAdapter(view: ViewPager2) :
    SimpleRecyclerAdapter2<List<AppImage>, ItemGalleryImageBinding>(view) {

    var onClickDeleteImage : ((AppImage) -> Unit)= {}

    var oldAfterPosition = -1
    var oldBeforePosition = -1

    override fun onCreateBinding(parent: ViewGroup): ItemGalleryImageBinding {
        return parent.bindingOf(ItemGalleryImageBinding::inflate)
    }

    fun updateList(list: List<AppImage>, position: Int){
        items?.set(position, list)
        notifyItemChanged(position)
    }

    override fun onBindHolder(
        item: List<AppImage>,
        binding: ItemGalleryImageBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            lvNoImage.show(item.isEmpty())
            lvShowImage.show(item.isNotEmpty())
            btnDelete.show(item.isNotEmpty())
            val adapter = ImageViewerPagerAdapter(vpImage).apply {
                submit(item)

            }
            vpImage.isUserInputEnabled = true
            btnDelete.onClick{
                if(adapterPosition == 0){
                    oldBeforePosition = adapter.findFirstVisibleItem()
                }else oldAfterPosition = adapter.findFirstVisibleItem()
                onClickDeleteImage.invoke(item[vpImage.currentItem])
            }

            vpImage.reduceDragSensitivity(4)

            vpImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                @SuppressLint("SetTextI18n")
                override fun onPageSelected(position: Int) {
                    tvCountImage.text = "${position + 1}/${item.size}"
                }
            })
            tvCountImage.text = "${1}/${item.size}"
            vpImage.setCurrentItem(  if(adapterPosition == 0) oldBeforePosition else oldAfterPosition, false)
        }
    }
}

fun ViewPager2.reduceDragSensitivity(f: Int = 4) {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    if(touchSlop > 100) return
    touchSlopField.set(recyclerView, touchSlop * f)       // "8" was obtained experimentally
}