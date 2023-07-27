package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentAccountBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.clients.SalonApi
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LogoutRepo
import com.app.inails.booking.admin.repository.client.AuthenticateRepository
import com.app.inails.booking.admin.views.me.adapters.AccountMultyOptionAdapter
import com.app.inails.booking.admin.views.me.adapters.AccountOption
import com.app.inails.booking.admin.views.me.dialogs.ChangeOwnerNameDialogOwner
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AccountFragment : BaseFragment(R.layout.fragment_account), TopBarOwner,
    ChangeOwnerNameDialogOwner {
    val viewModel by viewModel<AccountVM>()
    val binding by viewBinding(FragmentAccountBinding::bind)
    private lateinit var adapter: AccountMultyOptionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.state<MainTopBarState>().setTitle(R.string.title_account)

        val ownerList = listOf(
            AccountOption(R.string.title_change_password) {
                Router.open(
                    this@AccountFragment,
                    Routing.ChangePassword
                )
            },
            AccountOption(R.string.title_email_receive_feedback) {
                Router.open(
                    this@AccountFragment,
                    Routing.EmailReceiveFeedBack
                )
            },
            AccountOption(R.string.title_report) {
                Router.open(
                    this@AccountFragment,
                    Routing.ReportSale
                )
            },
            AccountOption(R.string.title_contact) {
                Router.open(
                    this@AccountFragment,
                    Routing.ContactAccount
                )
            },
            AccountOption(R.string.title_language) {
                Router.open(
                    this@AccountFragment,
                    Routing.SelectLanguageAccount
                )
            },
        )

        val manicuristList = listOf(
            AccountOption(R.string.title_edit_infor) {
                Router.open(
                    this@AccountFragment,
                    Routing.EditInforMani
                )
            },
            AccountOption(R.string.title_change_password) {
                Router.open(
                    this@AccountFragment,
                    Routing.ChangePassword
                )
            },
            AccountOption(R.string.title_contact) {
                Router.open(
                    this@AccountFragment,
                    Routing.ContactAccount
                )
            },
            AccountOption(R.string.title_language) {
                Router.open(
                    this@AccountFragment,
                    Routing.SelectLanguageAccount
                )
            },
        )

        with(binding) {
            adapter = AccountMultyOptionAdapter(rvLayout, userLocalSource.getAppMode()).apply {
                onClickChangeName = {
                    changeOwnerNameDialog.show(userLocalSource.getOwnerName()) {
                        viewModel.changeOwnerName(it)
                    }
                }
                onClickLogOut = {
                    confirmDialog.show(
                        title = R.string.title_logout_owner,
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
                updateDetailMani(viewModel.mani)
                updateOption(if (userLocalSource.isOwnerMode()) ownerList else manicuristList)
            }
            with(viewModel){
                successChangeName.bind{
                    success(R.string.success_change_owner_name)
                    adapter.updateDetailSalon(viewModel.user)
                }
            }
        }

        appEvent.notifyAccountApprovedAccount.bind { _ ->
            adapter.updateDetailSalon(viewModel.user)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateDetailSalon(viewModel.user)
    }
}

@Inject(ShareScope.Activity)
class ChangeOwnerNameRepo(
    private val salonApi: SalonApi,
    private val userLocalSource: UserLocalSource
) {
    suspend operator fun invoke(name: String) {
        salonApi.updatePartner(name).await()
        userLocalSource.changeOwnerName(name)
    }
}

class AccountVM(
    private val logoutRepo: LogoutRepo,
    private val userLocalSource: UserLocalSource,
    private val changeOwnerNameRepo: ChangeOwnerNameRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val successChangeName = SingleLiveEvent<Any>()

    fun logout() = launch(loading, error) {
        logoutRepo.invoke()
    }

    fun changeOwnerName(name: String) = launch(loading, error) {
        changeOwnerNameRepo.invoke(name)
        successChangeName.postValue(Any())
    }

    val user = userLocalSource.getUserDto()
    val mani = userLocalSource.getManicuristUser()
}

