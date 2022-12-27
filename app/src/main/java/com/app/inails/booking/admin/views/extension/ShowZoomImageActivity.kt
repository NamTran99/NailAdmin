package com.app.inails.booking.admin.views.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.FragmentShowMultyImageBinding
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize
import kotlin.properties.Delegates


@Parcelize
data class ShowZoomImageArgs1(
    val listImage: List<LocalImage>,
    val position: Int
) : BundleArgument


class ShowZoomImageActivity : BaseActivity(R.layout.fragment_show_multy_image), TopBarOwner {

    private lateinit var mImageViewerAdapter: ImageViewerAdapter
    private val binding by viewBinding(FragmentShowMultyImageBinding::bind)
    private val args by lazy { argument<ShowZoomImageArgs1>() }
    private lateinit var listImage: List<LocalImage>
    private var position by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listImage = args.listImage
        position = args.position

        with(binding) {
            mImageViewerAdapter = ImageViewerAdapter(vpPhoto).apply {
                items = listImage
                vpPhoto.currentItem = position
            }

            vpPhoto.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
                    tvCountImage.text = "${position + 1}/${listImage.size}"
                }
            })
            tvCountImage.text = "${position + 1}/${listImage.size}"
        }
    }
}