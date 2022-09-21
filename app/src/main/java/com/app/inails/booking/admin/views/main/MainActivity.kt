package com.app.inails.booking.admin.views.main

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityMainBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.alpha
import com.app.inails.booking.admin.extention.formatPhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LogoutRepo
import com.app.inails.booking.admin.repository.booking.AppointmentDetailRepository
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapterImpl
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(R.layout.activity_main), TopBarOwner,
    NavigationView.OnNavigationItemSelectedListener {
    override lateinit var topBar: TopBarAdapter

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topBar = TopBarAdapterImpl(this, findViewById(R.id.topBar))
        topBar.setState(MainTopBarState(R.string.title_dashboard, onMenuClick = {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }, onStaffListClick = {
            Router.run { redirectToStaffList() }
        }))
        with(binding) {
            navView.alpha(230)
            val headView = navView.getHeaderView(0)
            headView.findViewById<MaterialButton>(R.id.btMenuClose)
                .onClick { drawerLayout.closeDrawer(GravityCompat.START, true) }
            navView.setNavigationItemSelectedListener(this@MainActivity)

            headView.findViewById<TextView>(R.id.btMenuClose).text =
                "${viewModel.user?.admin?.salon?.name}\n${viewModel.user?.admin?.phone?.formatPhoneUS()}"

        }

        with(viewModel){
            detailAppointment.bind{

            }
        }

        appEvent.notifyCloudMessage.bind {
            with(viewModel){
                getDetailAppointment(it.data.appointment_id)
                typeNotifyDialog = it.type
            }
        }

        Router.run { redirectToBooking() }
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

}

class MainViewModel(
    private val logoutRepo: LogoutRepo,
    private val userLocalSource: UserLocalSource,
    private val appointmentDetailRepository: AppointmentDetailRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val detailAppointment = appointmentDetailRepository.result
    var typeNotifyDialog = NotifyFireBaseCloudType.CUSTOMER_CANCEL_APPOINTMENT

    fun getDetailAppointment(appointmentID: Int)= launch(loading, error) {
        appointmentDetailRepository.invoke(appointmentID)
    }

    val user = userLocalSource.getUserDto()
    fun logout() {
        logoutRepo.invoke()
    }
}
