package com.app.inails.booking.admin.views.main

//import com.github.arturogutierrez.Badges
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.route.clear
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.navigation.NavOptions
import android.support.navigation.Navigator
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.OWNER_ACCOUNT_APPROVE
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.ActivityMainBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.sockets.AuthSocket
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.helper.pairLookupOf
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.model.ui.NotificationIDForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToListRecruitment
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToLogin
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.notification.NotificationsManagerClient
import com.app.inails.booking.admin.repository.auth.LogoutRepo
import com.app.inails.booking.admin.views.booking.BookingFragment
import com.app.inails.booking.admin.views.clients.ClientHomeActivity
import com.app.inails.booking.admin.views.home.HomeFragment
import com.app.inails.booking.admin.views.main.dialogs.NotifyDialogOwner
import com.app.inails.booking.admin.views.me.AccountFragment
import com.app.inails.booking.admin.views.me.JobProfileFragment
import com.app.inails.booking.admin.views.notification.NotificationRepository
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapterImpl
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlin.reflect.KClass


class MainActivity : BaseActivity(R.layout.activity_main), TopBarOwner,
    NotifyDialogOwner {

    companion object {
        const val APPOINTMENT_ID = "Appointment_id"
        const val APPROVE_ACCOUNT = "Approve_account"

        fun getPendingIntent(
            context: Context,
            fireBaseMessage: FireBaseCloudMessage?
        ): PendingIntent? {
            if (fireBaseMessage == null) return null
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            if (fireBaseMessage.type.toInt() == OWNER_ACCOUNT_APPROVE) {
                intent.putExtra(APPROVE_ACCOUNT, true)
            } else {
                intent.putExtra(APPOINTMENT_ID, fireBaseMessage.data?.id ?: -1)
            }
            return PendingIntent.getActivity(
                context, 5, intent,
                PendingIntent.FLAG_IMMUTABLE
                        or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override lateinit var topBar: TopBarAdapter

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModel<MainViewModel>()
    private lateinit var mainTopBarState: MainTopBarState

    val route = pairLookupOf<Int, KClass<out BaseFragment>>(
        R.id.navMyCV to JobProfileFragment::class,
        R.id.navAccount to AccountFragment::class,
        R.id.navHome to HomeFragment::class,
    )

    private fun Navigator.navigateTo(id: Int): Boolean {
        val des = route.requireValue(id)
        val shouldNavigate = lastDestination?.kClass != des
        if (shouldNavigate) navigate(
            des, navOptions = NavOptions(
                popupTo = HomeFragment::class,
                reuseInstance = true,
                inclusive = false
            )
        )
        return shouldNavigate
    }

    var oldScreenItemID = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)

        NotificationsManagerClient(this).cancelAll()
        topBar = TopBarAdapterImpl(this, findViewById(R.id.topBar))
        mainTopBarState = MainTopBarState(R.string.title_dashboard, onMenuClick = {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }, onStaffListClick = {
            Router.run { redirectToStaffList() }
        }, onNotificationClick = {
            Router.open(this, Routing.Notification)
        }, isShowNoti = userLocalSource.isOwnerMode())
        topBar.setState(mainTopBarState)
        with(binding) {
            fabClientCheckIn.setText(if (userLocalSource.isOwnerMode()) R.string.customer_check_in else R.string.recruitment)
            fabClientCheckIn.onClick {
                if (userLocalSource.isOwnerMode()) {
                    if (userLocalSource.getUserDto()?.admin?.is_approve == 1) {
                        confirmDialog.show(
                            R.string.title_navigate_client_mode,
                            R.string.content_navigate_client_mode,
                            functionSubmit = {
                                userLocalSource.setAppMode(UserLocalSource.AppMode.Client)
                                Router.run {
                                    open<ClientHomeActivity>().clear()
                                }
                            },
                            functionCancel = {
                                bottomNavigation.selectedItemId = oldScreenItemID
                            })
                    } else {
                        notificationDialog.show(R.string.content_noty_salon_not_approve_check_in)
                    }
                } else {
                    redirectToListRecruitment()
                }
            }
            val navigator = findNavigator()
            navigator.addDestinationChangeListener {
                try {
                    bottomNavigation.selectedItemId =
                        route.requireKey(it as KClass<out BaseFragment>)
                } catch (_: java.lang.Exception) {
                }
            }
            bottomNavigation.menu.findItem(R.id.placeHolder).isEnabled = false
            bottomNavigation.menu.findItem(R.id.navMyCV).isVisible = !userLocalSource.isOwnerMode()
            bottomNavigation.menu.findItem(R.id.navHome).isVisible = userLocalSource.isOwnerMode()
            bottomNavigation.setOnItemSelectedListener {
                if (oldScreenItemID == it.itemId) return@setOnItemSelectedListener true
                when (it.itemId) {
                    R.id.navAppointment -> {
                        navigator.navigate(
                            BookingFragment::class,
                            Routing.BookingFragment(Routing.BookingFragment.TypeBooking.APPOINTMENTS)
                                .toBundle(),
                            navOptions = NavOptions(
                                popupTo = HomeFragment::class,
                                inclusive = false
                            )
                        )
                    }
                    R.id.placeHolder -> {
                        return@setOnItemSelectedListener true
                    }
                    else -> {
                        navigator.navigateTo(it.itemId)
                    }
                }
                oldScreenItemID = it.itemId
                return@setOnItemSelectedListener true
            }

            //first navigate
            if (userLocalSource.getAppMode() == UserLocalSource.AppMode.Owner) {
                navigator.navigateTo(R.id.navHome)
            } else navigator.navigateTo(R.id.navMyCV)
        }

        appEvent.notifyCloudMessage.bind { noti ->
            viewModel.numberNotificationSalonUnread()

            notifyDialog.onReadNotiListener = {
                viewModel.read(noti.id)
            }
            notifyDialog.show(noti,
                onClickViewDetailAppointment = { appointmentID ->
                    Router.open(this, Routing.AppointmentDetail(appointmentID))
                }
            )
        }

        appEvent.notifyAccountApproved.bind { isTrue ->
            if (isTrue) {
                viewModel.user?.admin?.is_approve = 1
                messageDialog.show(R.string.title_approve_account, R.string.content_approve_account)
            }
        }
        viewModel.count.bind {
            mainTopBarState.setNotificationUnreadCount(it)
        }
        viewModel.deleteAccount.bind {
            notificationDialog.show(R.string.unauthorized) {
                Router.run {
                    redirectToLogin()
                    viewModel.logout()
                }
            }
        }
    }

    fun navigateToTab(id: Int) {
        binding.bottomNavigation.selectedItemId = id
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val appointmentID = intent?.getIntExtra(APPOINTMENT_ID, -1) ?: -1
        val approveAccount = intent?.getBooleanExtra(APPROVE_ACCOUNT, false) ?: false
        if (approveAccount) {
            messageDialog.show(R.string.title_approve_account, R.string.content_approve_account)
        }
        if (appointmentID != -1) {
            Router.open(this, Routing.AppointmentDetail(appointmentID))
        }
    }

    override fun logout() {
        redirectToLogin()
        viewModel.logout()
    }

    override fun onBackPressed() {
        if (!findNavigator().navigateUp()) super.onBackPressed()
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        viewModel.numberNotificationSalonUnread()
        with(binding) {
//            tvSalonInfor.text =
//                "${viewModel.user?.admin?.salon?.name}\n${viewModel.user?.admin?.phone?.formatPhoneUSCustom()}"
//            tvVersion.text = Utils.getDisplayBuildConfig()
//            (viewModel.user?.admin?.is_approve == 0).show(tvSalonType)
        }
    }
}

class MainViewModel(
    private val logoutRepo: LogoutRepo,
    private val userLocalSource: UserLocalSource,
    private val notificationRepo: NotificationRepository,
    private val deleteAccountRepo: DeleteAccountRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val deleteAccount = deleteAccountRepo.deletedSalonAccount
    val count = notificationRepo.count
    val result = notificationRepo.result
    val idForm = NotificationIDForm()
    val user = userLocalSource.getUserDto()
    fun logout() = launch(loading, error) {
        logoutRepo.invoke()
    }

    fun numberNotificationSalonUnread() = launch {
        notificationRepo.numberNotificationSalonUnread()
    }

    fun read(id: Int) = launch {
        idForm.id = id
        notificationRepo.read(idForm)
        numberNotificationSalonUnread()
    }

}

@Inject(ShareScope.FragmentOrActivity)
class DeleteAccountRepo(authSocket: AuthSocket) {

    val deletedSalonAccount = SingleLiveEvent<Any>()

    init {
        authSocket.apply {

            observeSalonDeleteAccount {
                Log.e("----------> ", it[0].toString())
                deletedSalonAccount.call()
            }
        }
    }
}