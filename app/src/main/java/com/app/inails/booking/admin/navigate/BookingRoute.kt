package com.app.inails.booking.admin.navigate

import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.booking.BookingFragment

interface BookingRoute {
    fun BaseActivity.redirectToBooking()
}

class BookingRouteImpl : BookingRoute {
    override fun BaseActivity.redirectToBooking() {
        findNavigator().navigate(
            BookingFragment::class, navOptions = NavOptions(
                popupTo = BookingFragment::class,
                reuseInstance = true,
                inclusive = true
            )
        )
    }

}