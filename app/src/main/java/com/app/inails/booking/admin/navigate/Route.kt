package com.app.inails.booking.admin.navigate

import android.support.core.route.BundleArgument
import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.booking.create_appointment.CreateUpdateAppointmentFragment
import com.app.inails.booking.admin.views.booking.detail.AppointmentDetailFragment
import com.app.inails.booking.admin.views.main.MainNavigationActivity
import com.app.inails.booking.admin.views.management.customer.ManageCustomerFragment
import com.app.inails.booking.admin.views.management.service.ManageServiceFragment
import com.app.inails.booking.admin.views.management.staff.CheckInOutFragment
import com.app.inails.booking.admin.views.me.reset.ResetPasswordFragment
import com.app.inails.booking.admin.views.me.DetailSalonFragment
import com.app.inails.booking.admin.views.me.EmailReceiveFeedbackFragment
import com.app.inails.booking.admin.views.me.reset.OTPVerifyFragment
import com.app.inails.booking.admin.views.me.reset.ResetPasswordSuccessFragment
import com.app.inails.booking.admin.views.notification.NotificationFragment
import com.app.inails.booking.admin.views.report.ReportFragment
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass


interface Routing : BundleArgument {
    val fragmentClass: KClass<out Fragment>
    val options: NavOptions? get() = null

    @Parcelize
    object ManageStaff : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = CheckInOutFragment::class
    }

    @Parcelize
    class CreateAppointment(val id : Int?) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = CreateUpdateAppointmentFragment::class
    }

    @Parcelize
    object ChangePassword : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ResetPasswordFragment::class
    }

    @Parcelize
    object EmailReceiveFeedBack : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = EmailReceiveFeedbackFragment::class
    }

    @Parcelize
    object ManageService : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ManageServiceFragment::class
    }

    @Parcelize
    object DetailSalon : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = DetailSalonFragment::class
    }

    @Parcelize
    object ManageCustomer: Routing{
        override val fragmentClass: KClass<out Fragment>
            get() = ManageCustomerFragment::class
    }

    @Parcelize
    object ReportSale: Routing{
        override val fragmentClass: KClass<out Fragment>
            get() = ReportFragment::class
    }

    @Parcelize
    class AppointmentDetail(val id: Int) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = AppointmentDetailFragment::class
    }

    @Parcelize
    class ChooseStaff(val type : Int = 0,val dateTime : String?): Routing{
        override val fragmentClass: KClass<out Fragment>
            get() = ChooseStaffFragment::class
    }

    @Parcelize
    object Notification : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = NotificationFragment::class
    }

    @Parcelize
    object ResetPassword : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ResetPasswordFragment::class
    }

    @Parcelize
    class OTPVerify(val phoneNumber: String) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = OTPVerifyFragment::class
    }

    @Parcelize
    class ResetPasswordSuccess(val newPassword: String) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ResetPasswordSuccessFragment::class
    }
}

interface Router : SplashRoute, SettingRoute, BookingRoute, ManageStaffRoute, ManageCustomerRoute, ManageSalonRoute {
    fun open(dispatcher: RouteDispatcher, route: Routing)
    fun navigate(dispatcher: RouteDispatcher, route: Routing)

    companion object : Router by ProdRoute()
}

class ProdRoute : Router,
    SplashRoute by SplashRouteImpl(),
    SettingRoute by SettingRouteImpl(),
    BookingRoute by BookingRouteImpl(),
    ManageCustomerRoute by ManageCustomerRouteImpl(),
    ManageStaffRoute by ManageStaffRouteImpl(),
    ManageSalonRoute by ManageSalonRouteImpl(){
    override fun open(dispatcher: RouteDispatcher, route: Routing) {
        dispatcher.open<MainNavigationActivity>(route)
    }

    override fun navigate(dispatcher: RouteDispatcher, route: Routing) {
        val navigator = when (dispatcher) {
            is FragmentActivity -> dispatcher.findNavigator()
            is Fragment -> dispatcher.findNavigator()
            else -> error("Not found navigator")
        }
        navigator.navigate(route.fragmentClass, route.toBundle(), route.options)
    }
}