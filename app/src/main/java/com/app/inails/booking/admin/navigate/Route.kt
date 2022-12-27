package com.app.inails.booking.admin.navigate

import android.support.core.route.BundleArgument
import android.support.core.route.RouteDispatcher
import android.support.core.route.open
import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.navigate.clients.*
import com.app.inails.booking.admin.views.booking.create_appointment.ChooseStaffFragment
import com.app.inails.booking.admin.views.booking.create_appointment.CreateUpdateAppointmentFragment
import com.app.inails.booking.admin.views.booking.detail.AppointmentDetailFragment
import com.app.inails.booking.admin.views.clients.appointment.AppointmentDetailClientFragment
import com.app.inails.booking.admin.views.clients.feedbacks.SubmitFeedbackFragment
import com.app.inails.booking.admin.views.clients.media.PhotoViewerFragment
import com.app.inails.booking.admin.views.clients.profile.ProfileClientFragment
import com.app.inails.booking.admin.views.clients.salon.SalonDetailClientFragment
import com.app.inails.booking.admin.views.clients.salon.SalonGalleryFragment
import com.app.inails.booking.admin.views.extension.ShowZoomSingleImageFragment
import com.app.inails.booking.admin.views.main.MainNavigationActivity
import com.app.inails.booking.admin.views.main.StaffListFragment
import com.app.inails.booking.admin.views.management.customer.ManageCustomerFragment
import com.app.inails.booking.admin.views.management.service.ManageServiceFragment
import com.app.inails.booking.admin.views.management.staff.CheckInOutFragment
import com.app.inails.booking.admin.views.me.DetailSalonFragment
import com.app.inails.booking.admin.views.me.EmailReceiveFeedbackFragment
import com.app.inails.booking.admin.views.me.GalleryImageFragment
import com.app.inails.booking.admin.views.me.reset.*
import com.app.inails.booking.admin.views.notification.NotificationFragment
import com.app.inails.booking.admin.views.report.ReportFragment
import com.app.inails.booking.admin.views.splash.IntroFragment
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
    class AppointmentDetailClient(
        val idBooking: Long,
        val idNotification: Long? = 0,
        val salonID: Long
    ) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = AppointmentDetailClientFragment::class
    }


    @Parcelize
    object ProfileClient : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ProfileClientFragment::class
    }

    @Parcelize
    class SalonDetail(val id: Long = 0) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = SalonDetailClientFragment::class
    }

    @Parcelize
    class PhotoViewer(val photoUrl: String) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = PhotoViewerFragment::class
    }

    @Parcelize
    class SalonGallery(val id: Long = 0) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = SalonGalleryFragment::class
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
    object SignUpAccount : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = SignUpAccountFragment::class
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

    @Parcelize
    object StaffList : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = StaffListFragment::class
    }

    @Parcelize
    object Intro : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = IntroFragment::class
    }

    @Parcelize
    class GallerySalon(    val listBeforeImages: MutableList<AppImage>,
                            val listAfterImages: MutableList<AppImage>) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = GalleryImageFragment::class
    }

    @Parcelize
    class BookingFragment(val type: TypeBooking) : Routing {
        enum class TypeBooking{
            CHECK_IN, APPOINTMENTS
        }
        override val fragmentClass: KClass<out Fragment>
            get() = ResetPasswordSuccessFragment::class
    }

    @Parcelize
    class FeedBack(val bookingID: Long, val salonID: Long) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = SubmitFeedbackFragment::class
    }

    @Parcelize
    class ShowZoomSingleImage(val pathImage: String) : Routing {
        override val fragmentClass: KClass<out Fragment>
            get() = ShowZoomSingleImageFragment::class
    }
}

interface Router : SplashRoute,BookingRouteClient, SettingRoute, BookingRoute,ClientRoute,
    ManageStaffRoute, ManageCustomerRoute, ManageSalonRoute, ProfileRoute, SalonRoute, NotificationRouter {
    fun open(dispatcher: RouteDispatcher, route: Routing)
    fun navigate(dispatcher: RouteDispatcher, route: Routing)

    companion object : Router by ProdRoute()
}

class ProdRoute : Router,
    ClientRoute by ClientRouteImpl(),
    NotificationRouter by NotificationRouterImpl(),
    SalonRoute by SalonRouteImpl(),
    ProfileRoute by ProfileRouteImpl(),
    BookingRouteClient by BookingRouteClientImpl(),
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