package com.app.inails.booking.admin.views.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityWebViewBinding
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebViewArgs(
    val path: String,
    val content: String = "",
    val isHtml: Boolean = false
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
            if (!args.isHtml)
                webView.loadUrl(args.path)
            else {
              val text =  ("<html><style type='text/css'>@font-face { font-family: montserrat; src: url('file:///android_asset/montserrat.ttf'); } body p {font-family: montserrat;}" +
                        "img{display: inline; height: auto; max-width: 100%;}</style>"
                        + "<body style=\"background-color:white;\">" + "<p align=\"justify\" style=\"font-size: 14px; font-family: montserrat;  color:#000000;\">" +
                       args.content
                                + "</p> " + "</body></html>")
                webView.loadDataWithBaseURL("file:///android_asset/", text, "text/html", "UTF-8", null)
                webView.settings.textZoom = 70
            }
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