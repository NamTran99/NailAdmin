package com.app.inails.booking.admin.views.clients

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_ACCEPTED
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_CANCELLED
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_FINISHED
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_OWNER_CREATED
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_OWNER_UPDATE
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_REJECTED
import com.app.inails.booking.admin.DataConst.Notification.BOOKING_REMINDED
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityClientHomeBinding
import com.app.inails.booking.admin.datasource.remote.sockets.SocketClient
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.factory.client.FetchAllSalonRepository
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessageClient
import com.app.inails.booking.admin.model.firebase.NotificationBookingDTO
import com.app.inails.booking.admin.model.ui.client.IBookingNotification
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToEnterPhone
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.notification.NotificationsManager
import com.app.inails.booking.admin.repository.client.AuthenticateRepository
import com.app.inails.booking.admin.repository.client.DeleteAccountClientRepo
import com.app.inails.booking.admin.utils.Utils
import com.app.inails.booking.admin.views.clients.dialog.booking.BookingNotificationDialogOwner
import com.app.inails.booking.admin.views.clients.dialog.booking.BookingNotificationFinishedDialogOwner
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapterImpl
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.esafirm.imagepicker.helper.LocaleManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import kotlinx.parcelize.Parcelize

@Parcelize
class HomeArg(
    val type: Type = Type.BOOKING,
    val isChange: Boolean = false
) : BundleArgument {
    enum class Type {
        SELECT_SALON,
        BOOKING,
    }
}

class ClientHomeActivity : BaseActivity(R.layout.activity_client_home), TopBarOwner,
    NavigationView.OnNavigationItemSelectedListener,
    BookingNotificationDialogOwner,
    BookingNotificationFinishedDialogOwner {

    companion object {
        @SuppressLint("UnspecifiedImmutableFlag")
        fun getPendingIntent(
            context: Context,
            fireBaseMessage: FireBaseCloudMessageClient?
        ): PendingIntent? {
            if (fireBaseMessage == null) return null
            val data = fireBaseMessage.data?.toObject<NotificationBookingDTO>()
            val intent = Intent(context, ClientHomeActivity::class.java)
            intent.putExtra("salon_id", data?.salonId)
            intent.putExtra("id_notification", fireBaseMessage.id)
            intent.putExtra("id_booking", data?.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                            or PendingIntent.FLAG_ONE_SHOT
                )
            } else {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override lateinit var topBar: TopBarAdapter
    private val binding by viewBinding(ActivityClientHomeBinding::bind)
    private val viewModel by viewModel<MainVM>()

    private fun retrieveArgs(intent: Intent?): HomeArg {
        return BundleArgument.of(intent) ?: HomeArg()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        NotificationsManager(this).cancelAll()
        topBar = TopBarAdapterImpl(this, findViewById(R.id.topBar))
        with(appEvent) {
            openDrawer.bind { binding.drawerLayout.openDrawerLayout(true) }
            enableMenuLeft.bind { binding.drawerLayout.lockDrawerLayout(it) }
        }
        with(binding) {
            navView.alpha(230)
            val headView = navView.getHeaderView(0)
            headView.findViewById<MaterialButton>(R.id.btnMenuClose)
                .onClick { drawerLayout.closeDrawer(GravityCompat.START, true) }
            navView.setNavigationItemSelectedListener(this@ClientHomeActivity)
            tvVersion.text = Utils.getDisplayBuildConfig(this@ClientHomeActivity, userLocalSource.getLanguageWithDefault())
        }
        with(viewModel) {
            deletedCustomerAccount.bind {
                notificationDialog.show(R.string.auth_msg_deleted_account) {
                    viewModel.logout()
                }
            }
            deletedSalonAccount.bind {
                notificationDialog.show(R.string.auth_msg_deleted_account) {
                    viewModel.logout()
                }
            }
            logoutSuccess.bind { logoutSuccess() }
            showAcceptedDialog.bind {
                acceptedDialog.showAccepted(it) { openDetails(it.id, it.bookingID, it.salonID) }
            }
            showCancelledDialog.bind {
                cancelledDialog.showCancelled(it) { openDetails(it.id, it.bookingID, it.salonID) }
            }
            showNewDialog.bind {
                newDialog.showNew(it) {
                    openDetails(
                        it.id,
                        it.bookingID,
                        it.salonID
                    )
                }
            }
            showRemindUpcomingDialog.bind {
                upcomingDialog.showUpcoming(
                    it,
                    onDetail = { openDetails(it.id, it.bookingID, it.salonID) },
                    onDirection = { appSettings.navigateMyLocationWithGoogleMap(it.lat, it.lng) })
            }
            showFinishedDialog.bind {
                finishedDialog.show(it, onFeedback = { bookingID ->
                    Router.open(this@ClientHomeActivity, Routing.FeedBack(bookingID, it.salonID))
                })
            }
            showUpdateDialog.bind {
                updateDialog.showUpdate(
                    it,
                    onDetail = { openDetails(it.id, it.bookingID, it.salonID) }
                )
            }
            redirectDefault.bind { this@ClientHomeActivity.redirect() }
            redirect()
        }
        appEvent.notifyCloudMessageClient.bind { viewModel.notification(it) }
    }


    private fun logoutSuccess() {
        binding.drawerLayout.openDrawerLayout(false)
        notificationsManager.cancelAll()
        redirectToEnterPhone()
    }

    private fun redirect() {
        if (userLocalSource.getUserClientDto() == null) {
            Router.run { redirectToEnterPhone() }
        } else {
            Router.run { redirectToService() }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        try {
            if (intent?.extras == null) return
            val notificationID = intent.extras?.get("id_notification").toString().toLong()
            val bookingID = intent.extras?.get("id_booking").toString().toLong()
            val salonID = intent.extras?.get("salon_id").toString().toLong()
            openDetails(notificationID, bookingID, salonID)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun openDetails(notificationID: Long, bookingID: Long, salonID: Long) {
        Router.open(
            this@ClientHomeActivity,
            Routing.AppointmentDetailClient(bookingID, notificationID, salonID)
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navProfile -> {
                binding.drawerLayout.openDrawerLayout(false)
                Router.open(this, Routing.ProfileClient)
            }
            R.id.navAppointment -> {
                binding.drawerLayout.openDrawerLayout(false)
                Router.run { redirectToAppointment() }
            }
            R.id.navChangePassword -> {
                binding.drawerLayout.openDrawerLayout(false)
                Router.run { redirectToChangePassword() }
            }
            else -> confirmDialog.show(
                title = R.string.title_logout_client,
                message = R.string.message_logout_app,
                buttonConfirm = R.string.btn_yes_logout
            ) {
                viewModel.logout()
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (!findNavigator().navigateUp()) super.onBackPressed()
    }
}

class MainVM(
    private val authRepo: AuthenticateRepository,
    private val notificationRepo: NotificationRepository,
    private val salonRepository: FetchAllSalonRepository,
    deleteAccountRepo: DeleteAccountClientRepo,
    client: SocketClient
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    val showAcceptedDialog = SingleLiveEvent<IBookingNotification>()
    val showCancelledDialog = SingleLiveEvent<IBookingNotification>()
    val showNewDialog = SingleLiveEvent<IBookingNotification>()
    val showRemindUpcomingDialog = SingleLiveEvent<IBookingNotification>()
    val showFinishedDialog = SingleLiveEvent<IBookingNotification>()
    val showUpdateDialog = SingleLiveEvent<IBookingNotification>()
    val redirectToSalonSelector = SingleLiveEvent<Boolean>()
    val redirectDefault = SingleLiveEvent<Any>()
    val deletedCustomerAccount = deleteAccountRepo.deletedCustomerAccount
    val deletedSalonAccount = deleteAccountRepo.deletedSalonAccount

    init {
        client.connectIfNeed()
    }

    val logoutSuccess = SingleLiveEvent<Any>()
    fun logout() = launch(loading, error) {
        logoutSuccess.post(authRepo.logout())
    }

    fun notification(it: FireBaseCloudMessageClient) = launch(error = error) {
        when (it.type) {
            BOOKING_OWNER_CREATED -> showNewDialog.post(notificationRepo(it))
            BOOKING_ACCEPTED -> showAcceptedDialog.post(notificationRepo(it))
            BOOKING_CANCELLED, BOOKING_REJECTED -> showCancelledDialog.post(notificationRepo(it))
            BOOKING_REMINDED -> showRemindUpcomingDialog.post(notificationRepo(it))
            BOOKING_FINISHED -> showFinishedDialog.post(notificationRepo(it))
            BOOKING_OWNER_UPDATE -> showUpdateDialog.post(notificationRepo(it))
            else -> {}
        }

    }

    fun redirect() = launch(loading, error) {
        redirectDefault.call()
    }
}

@Inject(ShareScope.Activity)
class NotificationRepository(private val bookingFactory: BookingClientFactory) {
    operator fun invoke(it: FireBaseCloudMessageClient): IBookingNotification {
        return bookingFactory.createNotificationBooking(
            it.id,
            it.data?.toObject<NotificationBookingDTO>()
        )
    }
}
