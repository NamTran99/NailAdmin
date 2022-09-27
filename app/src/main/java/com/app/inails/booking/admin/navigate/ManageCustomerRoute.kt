package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.views.management.customer.CustomerBookingListFragment
import com.app.inails.booking.admin.views.management.customer.CustomerListBookingArg

interface ManageCustomerRoute {
    fun BaseFragment.redirectToCustomerBookingList(customerID: Int)
}

class ManageCustomerRouteImpl : ManageCustomerRoute {

    override fun BaseFragment.redirectToCustomerBookingList(customerID: Int) {
        findNavigator().navigate(
            CustomerBookingListFragment::class,
            args = CustomerListBookingArg(customerID).toBundle()
        )
    }
}