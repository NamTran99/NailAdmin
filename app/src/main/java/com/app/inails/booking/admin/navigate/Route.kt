package com.app.inails.booking.admin.navigate

import android.support.core.route.BundleArgument
import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.inails.booking.admin.views.main.MainNavigationActivity
import com.app.inails.booking.admin.views.manage_staff.ManageStaffFragment
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass


interface Routing : BundleArgument {
    val fragmentClass: KClass<out Fragment>
    val options: NavOptions? get() = null

    @Parcelize
    object ManageStaff : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ManageStaffFragment::class
    }
}

interface Router : SplashRoute, SettingRoute, BookingRoute,ManageStaffRoute {
    fun open(dispatcher: RouteDispatcher, route: Routing)
    fun navigate(dispatcher: RouteDispatcher, route: Routing)

    companion object : Router by ProdRoute()
}

class ProdRoute : Router,
    SplashRoute by SplashRouteImpl(),
    SettingRoute by SettingRouteImpl(),
    BookingRoute by BookingRouteImpl(),
    ManageStaffRoute by ManageStaffRouteImpl(){
    override fun open(dispatcher: RouteDispatcher, route: Routing) {
        dispatcher.open<MainNavigationActivity>(route)
    }

    override fun navigate(dispatcher: RouteDispatcher, route: Routing) {
        val navigator = when (dispatcher) {
            is FragmentActivity -> dispatcher.findNavigator()
            is Fragment -> dispatcher.findNavigator()
            else -> error("Not found navigator")
        }
        navigator.navigate(route.fragmentClass, route.toBundle(), route.options)
    }
}