package com.app.inails.booking.admin.views.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityWebViewBinding
import com.app.inails.booking.admin.views.extension.ImageViewerAdapter
import com.app.inails.booking.admin.views.extension.LocalImage
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs1
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize
import kotlin.properties.Delegates

@Parcelize
data class WebViewArgs(
    val path: String
) : BundleArgument


class WebViewActivity : BaseActivity(R.layout.activity_web_view), TopBarOwner {

    private val binding by viewBinding(ActivityWebViewBinding::bind)
    private val args by lazy { argument<WebViewArgs>() }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.settings.setSupportZoom(true)
            webView.loadUrl(args.path)
        }
    }

    override fun onBackPressed() {
        binding.apply {
            if (webView.canGoBack())
                webView.goBack()
            else
                super.onBackPressed()
        }
    }
}