package com.app.inails.booking.admin.navigate

import android.support.core.route.*
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.views.auth.LoginActivity
import com.app.inails.booking.admin.views.extension.ShowZoomImageActivity
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs1
import com.app.inails.booking.admin.views.home.WebViewActivity
import com.app.inails.booking.admin.views.home.WebViewArgs
import com.app.inails.booking.admin.views.main.MainActivity
import com.app.inails.booking.admin.views.me.CreateEditJobProfileFragment
import com.app.inails.booking.admin.views.youtube.YoutubeActivity
import com.app.inails.booking.admin.views.youtube.YoutubeActivityArgs

interface SplashRoute {
    fun RouteDispatcher.redirectToMain()
    fun RouteDispatcher.redirectToLogin()
    fun RouteDispatcher.redirectToCreateJobProfile()
    fun RouteDispatcher.redirectToEditJobProfile()
    fun RouteDispatcher.redirectToApmList()
    fun RouteDispatcher.redirectToListRecruitment()
    fun RouteDispatcher.redirectToShowZoomImage1(showZoomImageArgs: ShowZoomImageArgs1)
    fun RouteDispatcher.redirectToWebView(webViewArgs: WebViewArgs)
    fun RouteDispatcher.redirectToYouTube(path: String)
}

open class SplashRouteImpl : SplashRoute {
    override fun RouteDispatcher.redirectToListRecruitment() {
        Router.open(this, Routing.ListRecruitmentClient)
    }
    override fun RouteDispatcher.redirectToEditJobProfile() {
        Router.open(this, Routing.CreateEditJobProfile(isCreate = false))
    }
    override fun RouteDispatcher.redirectToCreateJobProfile() {
        Router.open(this, Routing.CreateEditJobProfile(isCreate = true))
    }

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

    override fun RouteDispatcher.redirectToYouTube(path: String) {
        val newPath = path.substring(path.lastIndexOf("/") + 1)
        open1<YoutubeActivity>(YoutubeActivityArgs(newPath))
    }

}