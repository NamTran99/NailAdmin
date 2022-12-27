package com.app.inails.booking.admin.navigate.clients

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.clients.profile.EditProfileFragment

interface ProfileRoute {
    fun BaseFragment.redirectToEdit()
}

class ProfileRouteImpl : ProfileRoute {

    override fun BaseFragment.redirectToEdit() {
        findNavigator().navigate(EditProfileFragment::class)
    }
}