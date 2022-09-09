package com.app.inails.booking.admin.navigate

interface Route : SplashRoute,SettingRoute,MainNavigateRoute {
		companion object : Route by DevRoute()
}

open class DevRoute : Route,
		SplashRoute by SplashRouteImpl(),
		SettingRoute by SettingRouteImpl(),
		MainNavigateRoute by MainNavigateRouteImpl()
