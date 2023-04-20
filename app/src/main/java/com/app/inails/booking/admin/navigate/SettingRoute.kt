package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.close
import android.support.core.route.open
import com.app.inails.booking.admin.views.auth.LoginActivity
import com.app.inails.booking.admin.views.auth.OptionLogin
import com.google.android.gms.common.api.Api.ApiOptions.Optional

interface SettingRoute {
    fun RouteDispatcher.closeAndOpenLogin()
    fun RouteDispatcher.redirectToLoginAndReturn(isEnableCloseAfterLogin: Boolean = true)
}

class SettingRouteImpl : SettingRoute {
    override fun RouteDispatcher.closeAndOpenLogin() {
        open<LoginActivity>().close()
    }

    override fun RouteDispatcher.redirectToLoginAndReturn(isEnableCloseAfterLogin: Boolean) {
        open<LoginActivity>(OptionLogin(isEnableCloseAfterLogin))
    }
}