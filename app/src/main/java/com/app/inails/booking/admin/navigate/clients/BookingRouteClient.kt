package com.app.inails.booking.admin.navigate.clients

import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.client.IServiceClient
import com.app.inails.booking.admin.navigate.BookingRoute
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.clients.appointment.AppointmentFragment
import com.app.inails.booking.admin.views.clients.booking.BookingArg
import com.app.inails.booking.admin.views.clients.booking.BookingServiceFragment
import com.app.inails.booking.admin.views.clients.booking.BookingStaffFragment
import com.app.inails.booking.admin.views.clients.booking.BookingSuccessFragment
import com.app.inails.booking.admin.views.main.MainNavigationActivity

interface BookingRouteClient {
    fun BaseActivity.redirectToService()
    fun BaseFragment.redirectToService()
    fun BaseActivity.redirectToAppointment()
    fun redirectToSubmitFeedback(self: RouteDispatcher, booingID: Long = 0, salonID: Long)
    fun BaseFragment.redirectToStaff(
        services: List<IServiceClient>,
        note: String
    )
    fun BaseFragment.redirectToSuccess()
}

class BookingRouteClientImpl : BookingRouteClient {

    override fun BaseActivity.redirectToService() {
        findNavigator().navigate(
            BookingServiceFragment::class, navOptions = NavOptions(
                popupTo = BookingServiceFragment::class,
                reuseInstance = true,
                inclusive = true
            )
        )
    }

    override fun BaseFragment.redirectToService() {
        findNavigator().navigate(
            BookingServiceFragment::class, navOptions = NavOptions(
                popupTo = BookingServiceFragment::class,
                reuseInstance = true,
                inclusive = true
            )
        )
    }
    override fun BaseActivity.redirectToAppointment() {
        findNavigator().navigate(AppointmentFragment::class)
    }
    override fun redirectToSubmitFeedback(self: RouteDispatcher, id: Long, salonID: Long) {
        self.open<MainNavigationActivity>(Routing.FeedBack(id, salonID))
    }

    override fun BaseFragment.redirectToStaff(
        services: List<IServiceClient>,
        note: String
    ) {
        findNavigator().navigate(
            BookingStaffFragment::class,
            args = BookingArg(
                services = services.map { it.id },
                note = note
            ).toBundle()
        )
    }

    override fun BaseFragment.redirectToSuccess() {
        findNavigator().navigate(BookingSuccessFragment::class)
    }


}