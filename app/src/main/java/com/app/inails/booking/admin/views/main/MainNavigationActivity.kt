package com.app.inails.booking.admin.views.main

import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.booking.create_appointment.CreateAppointmentFragment
import com.app.inails.booking.admin.views.manage_staff.ManageStaffFragment
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapterImpl
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class MainNavigationActivity : BaseActivity(R.layout.activity_main_navigation), TopBarOwner {
    override lateinit var topBar: TopBarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topBar = TopBarAdapterImpl(this, findViewById(R.id.topBar))
        val navigator = findNavigator()
        val args = argument<BundleArgument>()

        if (savedInstanceState == null) {
            val clazz = when (args) {
                is Routing.ManageStaff -> ManageStaffFragment::class
                is Routing.CreateAppointment -> CreateAppointmentFragment::class
                else -> error("Not support")
            }
            navigator.navigate(clazz, args = args.toBundle())
        }
    }

    override fun onBackPressed() {
        if (!findNavigator().navigateUp()) super.onBackPressed()
    }
}