package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.booking.BookingFragment
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.main.MainNavigationActivity

interface BookingRoute {
    fun BaseActivity.redirectToBooking()
    fun redirectToCreateAppointment(self: RouteDispatcher, id: Int? = null)
    fun BaseFragment.redirectToChooseStaff(id: Int? = null)
    fun redirectToAppointmentDetail(self: RouteDispatcher, id: Int)
    fun redirectToChooseStaff(self: RouteDispatcher, type: Int = 0, dateTime: String? = null)
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

    override fun BaseFragment.redirectToChooseStaff(
        id: Int?
    ) {
        findNavigator().navigate(
            ChooseStaffFragment::class
        )
    }

    override fun redirectToAppointmentDetail(self: RouteDispatcher, id: Int) {
        self.open<MainNavigationActivity>(Routing.AppointmentDetail(id))
    }

    override fun redirectToChooseStaff(self: RouteDispatcher, type: Int, dateTime: String?) {
        self.open<MainNavigationActivity>(Routing.ChooseStaff(type, dateTime))
    }

    override fun redirectToCreateAppointment(self: RouteDispatcher, id: Int?) {
        self.open<MainNavigationActivity>(Routing.CreateAppointment(id))
    }
}