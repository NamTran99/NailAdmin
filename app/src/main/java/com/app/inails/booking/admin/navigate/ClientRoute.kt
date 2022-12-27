package com.app.inails.booking.admin.navigate

import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.clients.CheckInFragment

interface ClientRoute {
    fun BaseFragment.redirectToEnterPhone()
    fun BaseActivity.redirectToEnterPhone()

}

class ClientRouteImpl : ClientRoute {
    override fun BaseFragment.redirectToEnterPhone() {
        findNavigator().navigate(CheckInFragment::class, navOptions = animOption)
    }
    override fun BaseActivity.redirectToEnterPhone() {
        findNavigator().navigate(CheckInFragment::class, navOptions = animOption)
    }
}

private val animOption = NavOptions(
    animEnter = R.anim.slide_up_show,
    animExit = R.anim.slide_out_down,
    animPopEnter = R.anim.slide_up_show,
    animPopExit = R.anim.slide_out_down
)
