package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.me.EditScheduleArgs
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.UpdateSalonFragment

interface ManageSalonRoute {
    fun BaseFragment.redirectToUpdateSalon()
    fun BaseFragment.redirectToEditScheduleSalon(editScheduleArgs: EditScheduleArgs)
}

class ManageSalonRouteImpl : ManageSalonRoute {
    override fun BaseFragment.redirectToUpdateSalon() {
        findNavigator().navigate(UpdateSalonFragment::class)
    }

    override fun BaseFragment.redirectToEditScheduleSalon(editScheduleArgs: EditScheduleArgs) {
        findNavigator().navigate(EditScheduleFragment::class, args = editScheduleArgs.toBundle())
    }
}