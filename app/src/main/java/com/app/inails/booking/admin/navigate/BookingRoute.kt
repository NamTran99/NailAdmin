package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.booking.BookingFragment
import com.app.inails.booking.admin.views.booking.create_appointment.AppointmentArg
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.booking.create_appointment.CreateAppointmentFragment
import com.app.inails.booking.admin.views.booking.detail.AppointmentDetailFragment
import com.app.inails.booking.admin.views.main.MainNavigationActivity

interface BookingRoute {
    fun BaseActivity.redirectToBooking()
    fun BaseFragment.redirectToCreateAppointment(id: Int? = null)
    fun BaseFragment.redirectToChooseStaff(id: Int? = null)
    fun redirectToAppointmentDetail(self: RouteDispatcher, id:Int)
    fun redirectToChooseStaff(self: RouteDispatcher, type : Int = 0)
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

    override fun BaseFragment.redirectToCreateAppointment(
        id: Int?
    ) {
        findNavigator().navigate(
            CreateAppointmentFragment::class,
            args = AppointmentArg(id = id).toBundle()
        )
    }

    override fun BaseFragment.redirectToChooseStaff(
        id: Int?) {
        findNavigator().navigate(
            ChooseStaffFragment::class
        )
    }

    override fun redirectToAppointmentDetail(self: RouteDispatcher, id: Int) {
        self.open<MainNavigationActivity>(Routing.AppointmentDetail(id))
    }

    override fun redirectToChooseStaff(self: RouteDispatcher, type: Int) {
        self.open<MainNavigationActivity>(Routing.ChooseStaff(type))
    }
}