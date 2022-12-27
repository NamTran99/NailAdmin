package com.app.inails.booking.admin.views.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentShowMultyImageBinding
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize


@Parcelize
data class ShowZoomImageArgs(
    val data: Pair<List<LocalImage>, Int>
) : BundleArgument
@Parcelize
data class LocalImage(
    val path: String = ""
): Parcelable

class ShowZoomListImageFragment : BaseFragment(R.layout.fragment_show_multy_image), TopBarOwner {

    private lateinit var mImageViewerAdapter: ImageViewerAdapter
    private val binding by viewBinding(FragmentShowMultyImageBinding::bind)
    private val args by lazy { argument<ShowZoomImageArgs>() }
    private lateinit var data: Pair<List<LocalImage>, Int>

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_salon_image,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        data = args.data

        with(binding) {
            mImageViewerAdapter = ImageViewerAdapter(vpPhoto).apply {
                items = data.first
                vpPhoto.currentItem = data.second
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
                    tvCountImage.text = "${position + 1}/${data.first.size}"
                }
            })
            tvCountImage.text = "${data.second + 1}/${data.first.size}"
        }
    }
}