package com.app.inails.booking.admin.navigate

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.extension.ShowZoomListImageFragment
import com.app.inails.booking.admin.views.extension.ShowZoomSingleImageFragment
import com.app.inails.booking.admin.views.me.EditScheduleArgs
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.UpdateSalonFragment
import com.app.inails.booking.admin.views.me.VoucherApplyFragment

interface ManageSalonRoute {
    fun BaseFragment.redirectToUpdateSalon()
    fun BaseFragment.redirectToEditScheduleSalon(editScheduleArgs: EditScheduleArgs)
    fun BaseFragment.redirectToApplyVoucherSalon(args: Routing.VoucherApply)
}

class ManageSalonRouteImpl : ManageSalonRoute {
    override fun BaseFragment.redirectToUpdateSalon() {
        findNavigator().navigate(UpdateSalonFragment::class)
    }

    override fun BaseFragment.redirectToEditScheduleSalon(editScheduleArgs: EditScheduleArgs) {
        findNavigator().navigate(EditScheduleFragment::class, args = editScheduleArgs.toBundle())
    }

    override fun BaseFragment.redirectToApplyVoucherSalon(args: Routing.VoucherApply) {
        findNavigator().navigate(VoucherApplyFragment::class, args = args.toBundle())
    }
}