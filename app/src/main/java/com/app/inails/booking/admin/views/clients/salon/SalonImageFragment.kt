package com.app.inails.booking.admin.views.clients.salon

import android.os.Bundle
import android.support.core.view.viewBinding
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSalonImageBinding
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.views.clients.dialog.view_image.ImagesViewPagerAdapter

class SalonImageFragment(val images:List<String>) : BaseFragment(R.layout.fragment_salon_image) {
    private val binding by viewBinding(FragmentSalonImageBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            ImagesViewPagerAdapter(vpgImages).apply {
                items = images
            }

            txtCount.text = "1/${images.size}"
            txtCount.show(images.isNotEmpty())
            lvNoImage.show(images.isEmpty())
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
        }
    }
}