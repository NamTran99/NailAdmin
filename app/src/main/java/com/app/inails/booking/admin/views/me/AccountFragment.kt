package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentAccountBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LogoutRepo
import com.app.inails.booking.admin.views.me.adapters.AccountMultyOptionAdapter
import com.app.inails.booking.admin.views.me.adapters.AccountOption
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AccountFragment : BaseFragment(R.layout.fragment_account), TopBarOwner {
    val viewModel by viewModel<AccountVM>()
    val binding by viewBinding(FragmentAccountBinding::bind)
    private  lateinit var  adapter: AccountMultyOptionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.state<MainTopBarState>().setTitle(R.string.title_account)

        with(binding) {
            adapter = AccountMultyOptionAdapter(rvLayout).apply {
                onClickLogOut = {
                    confirmDialog.show(
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
                updateDetailSalon(viewModel.user)
                updateOption(listOf(
                    AccountOption(R.string.title_change_password) {   Router.open(this@AccountFragment, Routing.ChangePassword)},
                    AccountOption(R.string.title_email_receive_feedback) {   Router.open(this@AccountFragment, Routing.EmailReceiveFeedBack)},
                    AccountOption(R.string.title_report) {   Router.open(this@AccountFragment, Routing.ReportSale)},
                ))
            }
        }

        appEvent.notifyAccountApproved.bind { _ ->
            adapter.updateDetailSalon(viewModel.user)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateDetailSalon(viewModel.user)
    }
}
class AccountVM(
    private val logoutRepo: LogoutRepo,
    private val userLocalSource: UserLocalSource,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    fun logout() = launch(loading, error) {
        logoutRepo.invoke()
    }
    val user = userLocalSource.getUserDto()
}

