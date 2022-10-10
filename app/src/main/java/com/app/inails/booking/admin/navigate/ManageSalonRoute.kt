package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.UpdateSalonFragment

interface ManageSalonRoute {
    fun BaseFragment.redirectToUpdateSalon()
    fun BaseFragment.redirectToEditScheduleSalon()
}

class ManageSalonRouteImpl : ManageSalonRoute {
    override fun BaseFragment.redirectToUpdateSalon() {
        findNavigator().navigate(UpdateSalonFragment::class)
    }

    override fun BaseFragment.redirectToEditScheduleSalon() {
        findNavigator().navigate(EditScheduleFragment::class)
    }
}