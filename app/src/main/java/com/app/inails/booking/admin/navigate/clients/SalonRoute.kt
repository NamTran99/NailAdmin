package com.app.inails.booking.admin.navigate.clients

import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.clients.feedbacks.FeedbackArg
import com.app.inails.booking.admin.views.clients.feedbacks.FeedbackFragment

interface SalonRoute {
    fun BaseFragment.redirectToFeedback(salonID: Long = 0)
}

class SalonRouteImpl : SalonRoute {
    override fun BaseFragment.redirectToFeedback(salonID: Long) {
        findNavigator().navigate(
            FeedbackFragment::class, args = FeedbackArg(salonID).toBundle()
        )
    }
}