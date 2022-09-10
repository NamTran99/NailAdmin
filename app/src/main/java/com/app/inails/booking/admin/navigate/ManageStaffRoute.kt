package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.manage_staff.ManageStaffFragment

interface ManageStaffRoute {
    fun BaseFragment.redirectToEdit()
}

class ManageStaffRouteImpl : ManageStaffRoute {

    override fun BaseFragment.redirectToEdit() {
        findNavigator().navigate(ManageStaffFragment::class)
    }
}