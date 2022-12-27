package com.app.inails.booking.admin.navigate.clients

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.clients.notification.NotificationClientFragment
import com.app.inails.booking.admin.views.notification.NotificationFragment

interface NotificationRouter {
    fun BaseFragment.redirectToNotification()
}

class NotificationRouterImpl : NotificationRouter {
    override fun BaseFragment.redirectToNotification() {
        findNavigator().navigate(NotificationClientFragment::class)
    }
}