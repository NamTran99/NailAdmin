package com.app.inails.booking.admin.views.clients.dialog.view_image

import android.content.Context
import android.support.core.view.ViewScopeOwner
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogViewImagesBinding
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.views.base.ImagesViewPagerAdapter


class ViewImageDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogViewImagesBinding::inflate,false)

    init {
        setCanceledOnTouchOutside(false)
        binding.btnBack.onClick {
            dismiss()
        }
    }

    fun shows(
        index: Int = 0,
        images: List<String>
    ) = lock(binding) {
        ImagesViewPagerAdapter(vpgImages).apply {
            items = images
        }
        txtCount.text = "${index + 1}/${images.size}"
        binding.vpgImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                txtCount.text = "${position + 1}/${images.size}"
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        binding.vpgImages.postDelayed(Runnable { binding.vpgImages.currentItem = index }, 10)
        show()
    }

    fun show(
        image: String
    ) = lock(binding) {
        val images = mutableListOf<String>()
        images.add(image)
        ImagesViewPagerAdapter(vpgImages).apply {
            items = images
        }
        txtCount.text = "1/1"
        show()
    }
}


interface ViewImageDialogOwner : ViewScopeOwner {
    val viewImagesDialog: ViewImageDialog
        get() = with(viewScope) {
            getOr("view_image:dialog") { ViewImageDialog(context) }
        }
}