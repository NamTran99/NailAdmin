package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.views.management.customer.CustomerBookingListFragment
import com.app.inails.booking.admin.views.management.customer.CustomerListBookingArg
import com.app.inails.booking.admin.views.management.customer.TypeID
import com.app.inails.booking.admin.views.management.staff.CheckInOutArg
import com.app.inails.booking.admin.views.management.staff.CheckInOutFragment

interface ManageCustomerRoute {
    fun BaseFragment.redirectToCustomerBookingList(customerID: Int)
    fun BaseFragment.redirectToStaffBookingList(staffID: Int)
    fun BaseFragment.redirectToStaffCheckInHistory(staffID: Int)
}

class ManageCustomerRouteImpl : ManageCustomerRoute {

    override fun BaseFragment.redirectToCustomerBookingList(customerID: Int) {
        findNavigator().navigate(
            CustomerBookingListFragment::class,
            args = CustomerListBookingArg(customerID, TypeID.Customer).toBundle()
        )
    }

    override fun BaseFragment.redirectToStaffBookingList(staffID: Int) {
        findNavigator().navigate(
            CustomerBookingListFragment::class,
            args = CustomerListBookingArg(staffID, TypeID.Staff).toBundle()
        )
    }

    override fun BaseFragment.redirectToStaffCheckInHistory(staffID: Int) {
        findNavigator().navigate(
            CheckInOutFragment::class,
            args = CheckInOutArg(staffID).toBundle()
        )
    }
}