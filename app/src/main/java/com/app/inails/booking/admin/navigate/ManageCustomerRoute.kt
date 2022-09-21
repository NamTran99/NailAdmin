package com.app.inails.booking.admin.navigate

import android.support.core.route.RouteDispatcher
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.views.management.customer.CustomerBookingListFragment
import com.app.inails.booking.admin.views.management.customer.CustomerListBookingArg
import com.app.inails.booking.admin.views.management.staff.ManageStaffFragment

interface ManageCustomerRoute {
    fun BaseFragment.redirectToCustomerBookingList(list: List<ServiceImpl>)
//    fun redirectToListBooking(self: RouteDispatcher, list: List<ServiceDTO>)
}

class ManageCustomerRouteImpl : ManageCustomerRoute {

    override fun BaseFragment.redirectToCustomerBookingList(list: List<ServiceImpl>) {
        findNavigator().navigate(CustomerBookingListFragment::class, args = CustomerListBookingArg(list).toBundle())
    }

//    override fun redirectToListBooking(self: RouteDispatcher, list: List<ServiceDTO>) {
//
//    }

}