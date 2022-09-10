package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.close
import android.support.core.route.open
import com.app.inails.booking.admin.views.auth.LoginActivity
import com.app.inails.booking.admin.views.main.MainActivity

interface SplashRoute {
    fun RouteDispatcher.redirectToMain()
    fun RouteDispatcher.redirectToLogin()
}

open class SplashRouteImpl : SplashRoute {
    override fun RouteDispatcher.redirectToMain() {
        open<MainActivity>().close()
    }

    override fun RouteDispatcher.redirectToLogin() {
        open<LoginActivity>().close()
    }
}