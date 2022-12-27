package com.app.inails.booking.admin.views.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentShowSingleImageBinding
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.PinchZoomImageView
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ShowZoomSingleImageActivity : BaseActivity(R.layout.fragment_show_single_image) {
    private val binding by viewBinding(FragmentShowSingleImageBinding::bind)
    private val args by lazy { argument<Routing.ShowZoomSingleImage>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            loadImage(imgZoom, progressBar, args.pathImage)
        }
    }

    private fun loadImage(imageView: PinchZoomImageView, loading: ProgressBar, image: String) {
        showLoading(imageView, loading)
        imageView.setImageUrl(image) { showImage(imageView, loading) }
    }

    private fun showImage(imageView: ImageView, loading: ProgressBar) {
        imageView.visibility = View.VISIBLE
        loading.visibility = View.GONE
    }

    private fun showLoading(imageView: ImageView, loading: ProgressBar) {
        imageView.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }
}