package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.close
import android.support.core.route.open
import com.app.inails.booking.admin.views.auth.LoginActivity

interface SettingRoute {
    fun RouteDispatcher.closeAndOpenLogin()
}

class SettingRouteImpl : SettingRoute {
    override fun RouteDispatcher.closeAndOpenLogin() {
        open<LoginActivity>().close()
    }
}