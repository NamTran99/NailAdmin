package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.main.StaffListFragment
import com.app.inails.booking.admin.views.manage_staff.ManageStaffFragment

interface ManageStaffRoute {
    fun BaseFragment.redirectToList()
    fun BaseActivity.redirectToStaffList()
}

class ManageStaffRouteImpl : ManageStaffRoute {

    override fun BaseFragment.redirectToList() {
        findNavigator().navigate(ManageStaffFragment::class)
    }

    override fun BaseActivity.redirectToStaffList() {
        findNavigator().navigate(
            StaffListFragment::class
        )
    }
}