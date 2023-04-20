package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.model.ui.CustomerImpl
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.booking.create_appointment.FindCustomer
import com.app.inails.booking.admin.views.booking.create_appointment.FindCustomerActivity
import com.app.inails.booking.admin.views.clients.auth.ChangePasswordClientFragment
import com.app.inails.booking.admin.views.main.MainNavigationActivity

interface BookingRoute {
    fun redirectToCreateAppointment(self: RouteDispatcher, id: Int? = null)
    fun BaseFragment.redirectToChooseStaff(id: Int? = null)
    fun redirectToAppointmentDetail(self: RouteDispatcher, id: Int)
    fun redirectToChooseStaff(self: RouteDispatcher, type: Int = 0, dateTime: String? = null)
    fun BaseActivity.redirectToChangePassword()
    fun redirectToFindCustomer(self: RouteDispatcher, phone: String = "")
}

class BookingRouteImpl : BookingRoute {


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

    override fun BaseActivity.redirectToChangePassword() {
        findNavigator().navigate(ChangePasswordClientFragment::class)
    }

    override fun redirectToFindCustomer(self: RouteDispatcher, phone: String) {
        self.open<FindCustomerActivity>(FindCustomer(phone))
    }

}