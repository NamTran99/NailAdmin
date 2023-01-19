package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.clear
import android.support.core.route.close
import android.support.core.route.open
import com.app.inails.booking.admin.views.auth.LoginActivity
import com.app.inails.booking.admin.views.extension.ShowZoomImageActivity
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs1
import com.app.inails.booking.admin.views.home.WebViewActivity
import com.app.inails.booking.admin.views.home.WebViewArgs
import com.app.inails.booking.admin.views.main.MainActivity

interface SplashRoute {
    fun RouteDispatcher.redirectToMain()
    fun RouteDispatcher.redirectToLogin()
    fun RouteDispatcher.redirectToApmList()
    fun RouteDispatcher.redirectToShowZoomImage1(showZoomImageArgs: ShowZoomImageArgs1)
    fun RouteDispatcher.redirectToWebView(webViewArgs: WebViewArgs)
}

open class SplashRouteImpl : SplashRoute {
    override fun RouteDispatcher.redirectToMain() {
        open<MainActivity>().clear()
    }

    override fun RouteDispatcher.redirectToLogin() {
        open<LoginActivity>().clear()
    }

    override fun RouteDispatcher.redirectToApmList() {
        open<MainActivity>().clear()
        Router.open(
            this,
            Routing.BookingFragment(Routing.BookingFragment.TypeBooking.APPOINTMENTS)
        )
    }

    override fun RouteDispatcher.redirectToShowZoomImage1(showZoomImageArgs: ShowZoomImageArgs1) {
        open<ShowZoomImageActivity>(showZoomImageArgs)
    }

    override fun RouteDispatcher.redirectToWebView(webViewArgs: WebViewArgs) {
        open<WebViewActivity>(webViewArgs)
    }

}