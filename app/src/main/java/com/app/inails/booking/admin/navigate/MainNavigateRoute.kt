package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.open

interface MainNavigateRoute {
		fun openUserDetail(self: RouteDispatcher, email: String)
}

class MainNavigateRouteImpl: MainNavigateRoute{
		override fun openUserDetail(self: RouteDispatcher, email: String) {
//				self.open<MainNavigationActivity>(Routing.UserDetail(email))
		}
		
}