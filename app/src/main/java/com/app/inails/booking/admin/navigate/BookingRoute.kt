package com.app.inails.booking.admin.navigate

import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.booking.BookingFragment
import com.app.inails.booking.admin.views.booking.create_appointment.AppointmentArg
import com.app.inails.booking.admin.views.booking.create_appointment.CreateAppointmentFragment

interface BookingRoute {
    fun BaseActivity.redirectToBooking()
    fun BaseFragment.redirectToCreateAppointment(id: Int? = null)
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
}