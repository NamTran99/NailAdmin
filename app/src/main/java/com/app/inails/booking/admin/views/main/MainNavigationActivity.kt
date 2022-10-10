package com.app.inails.booking.admin.views.main

import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.booking.create_appointment.CreateUpdateAppointmentFragment
import com.app.inails.booking.admin.views.booking.detail.AppointmentDetailFragment
import com.app.inails.booking.admin.views.main.dialogs.NotifyDialogOwner
import com.app.inails.booking.admin.views.management.customer.ManageCustomerFragment
import com.app.inails.booking.admin.views.management.service.ManageServiceFragment
import com.app.inails.booking.admin.views.management.staff.CheckInOutFragment
import com.app.inails.booking.admin.views.management.staff.ManageStaffFragment
import com.app.inails.booking.admin.views.me.ChangePasswordFragment
import com.app.inails.booking.admin.views.me.reset.ResetPasswordFragment
import com.app.inails.booking.admin.views.me.DetailSalonFragment
import com.app.inails.booking.admin.views.me.EmailReceiveFeedbackFragment
import com.app.inails.booking.admin.views.me.reset.OTPVerifyFragment
import com.app.inails.booking.admin.views.me.reset.ResetPasswordSuccessFragment
import com.app.inails.booking.admin.views.notification.NotificationFragment
import com.app.inails.booking.admin.views.report.ReportFragment
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapterImpl
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class MainNavigationActivity : BaseActivity(R.layout.activity_main_navigation), TopBarOwner,
     NotifyDialogOwner {
    override lateinit var topBar: TopBarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topBar = TopBarAdapterImpl(this, findViewById(R.id.topBar))
        val navigator = findNavigator()
        val args = argument<BundleArgument>()

        if (savedInstanceState == null) {
            val clazz = when (args) {
                is Routing.ManageStaff -> ManageStaffFragment::class
                is Routing.CreateAppointment -> CreateUpdateAppointmentFragment::class
                is Routing.ChangePassword -> ChangePasswordFragment::class
                is Routing.EmailReceiveFeedBack -> EmailReceiveFeedbackFragment::class
                is Routing.ManageService -> ManageServiceFragment::class
                is Routing.AppointmentDetail -> AppointmentDetailFragment::class
                is Routing.ManageCustomer -> ManageCustomerFragment::class
                is Routing.ChooseStaff -> ChooseStaffFragment::class
                is Routing.ReportSale -> ReportFragment::class
                is Routing.Notification -> NotificationFragment::class
                is Routing.DetailSalon -> DetailSalonFragment::class
                is Routing.ResetPassword -> ResetPasswordFragment::class
                is Routing.OTPVerify -> OTPVerifyFragment::class
                is Routing.ResetPasswordSuccess -> ResetPasswordSuccessFragment::class
                else -> error("Not support")
            }
            navigator.navigate(clazz, args = args.toBundle())
        }

        appEvent.notifyCloudMessage.bind {
            notifyDialog.show(it,
                onClickViewDetailAppointment = { appointmentID ->
                    Router.open(this, Routing.AppointmentDetail(appointmentID))
                }
            )
        }
    }

    override fun onBackPressed() {
        if (!findNavigator().navigateUp()) super.onBackPressed()
    }
}