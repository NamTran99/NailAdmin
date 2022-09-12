package com.app.inails.booking.admin.views.main

import android.os.Bundle
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityMainBinding
import com.app.inails.booking.admin.extention.alpha
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
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
        }
        Router.run { redirectToBooking() }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navManageService -> {

            }
            R.id.navManageStaff -> {
                Router.open(this, Routing.ManageStaff)
            }
            R.id.navManageCustomer -> {

            }
            R.id.navEmailReceiveFeedback -> {
                Router.open(this, Routing.EmailReceiveFeedBack)
            }
            R.id.navChangePassword -> {
                Router.open(this, Routing.ChangePassword)
            }
            R.id.navReport -> {

            }
            else -> confirmDialog.show(
                title = R.string.title_logout,
                message = R.string.message_logout_app,
                buttonConfirm = R.string.btn_yes_logout
            ) {
                Router.run { redirectToLogin() }
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (!findNavigator().navigateUp()) super.onBackPressed()
    }
}
