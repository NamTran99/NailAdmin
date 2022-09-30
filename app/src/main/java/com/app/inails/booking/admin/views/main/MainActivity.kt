package com.app.inails.booking.admin.views.main

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityMainBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.alpha
import com.app.inails.booking.admin.extention.formatPhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LogoutRepo
import com.app.inails.booking.admin.views.main.dialogs.NotifyDialogOwner
import com.app.inails.booking.admin.views.notification.NotificationRepository
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapterImpl
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(R.layout.activity_main), TopBarOwner,
    NavigationView.OnNavigationItemSelectedListener, NotifyDialogOwner {

    companion object {
        const val APPOINTMENT_ID = "Appointment_id"

        fun getPendingIntent(
            context: Context,
            fireBaseMessage: FireBaseCloudMessage?
        ): PendingIntent? {
            if (fireBaseMessage == null) return null
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(APPOINTMENT_ID, fireBaseMessage.data.id)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        topBar = TopBarAdapterImpl(this, findViewById(R.id.topBar))
        mainTopBarState = MainTopBarState(R.string.title_dashboard, onMenuClick = {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }, onStaffListClick = {
            Router.run { redirectToStaffList() }
        }, onNotificationClick = {
            Router.open(this, Routing.Notification)
        })
        topBar.setState(mainTopBarState)
        with(binding) {
            navView.alpha(230)
            val headView = navView.getHeaderView(0)
            headView.findViewById<MaterialButton>(R.id.btMenuClose)
                .onClick { drawerLayout.closeDrawer(GravityCompat.START, true) }
            navView.setNavigationItemSelectedListener(this@MainActivity)

            headView.findViewById<TextView>(R.id.btMenuClose).text =
                "${viewModel.user?.admin?.salon?.name}\n${viewModel.user?.admin?.phone?.formatPhoneUS()}"
        }
        appEvent.notifyCloudMessage.bind {
            notifyDialog.show(it,
                onClickViewDetailAppointment = { appointmentID ->
                    Router.open(this, Routing.AppointmentDetail(appointmentID))
                }
            )
        }
        Router.run { redirectToBooking() }
        viewModel.count.bind {
            mainTopBarState.setNotificationUnreadCount(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val appointmentID = intent?.getIntExtra(APPOINTMENT_ID, -1) ?: -1
        if (appointmentID != -1) {
            Router.open(this, Routing.AppointmentDetail(appointmentID))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navManageService -> {
                Router.open(this, Routing.ManageService)
            }
            R.id.navManageStaff -> {
                Router.open(this, Routing.ManageStaff)
            }
            R.id.navManageCustomer -> {
                Router.open(this, Routing.ManageCustomer)
            }
            R.id.navEmailReceiveFeedback -> {
                Router.open(this, Routing.EmailReceiveFeedBack)
            }
            R.id.navChangePassword -> {
                Router.open(this, Routing.ChangePassword)
            }
            R.id.navReport -> {
                Router.open(this, Routing.ReportSale)
            }
            else -> confirmDialog.show(
                title = R.string.title_logout,
                message = R.string.message_logout_app,
                buttonConfirm = R.string.btn_yes_logout
            ) {
                Router.run {
                    redirectToLogin()
                    viewModel.logout()
                }
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (!findNavigator().navigateUp()) super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.numberNotificationSalonUnread()
    }

}

class MainViewModel(
    private val logoutRepo: LogoutRepo,
    private val userLocalSource: UserLocalSource,
    private val notificationRepo: NotificationRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val count = notificationRepo.count

    val user = userLocalSource.getUserDto()
    fun logout() = launch(loading, error) {
        logoutRepo.invoke()
    }

    fun numberNotificationSalonUnread() = launch {
        notificationRepo.numberNotificationSalonUnread()
    }
}
