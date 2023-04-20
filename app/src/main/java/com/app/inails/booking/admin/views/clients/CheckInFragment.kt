package com.app.inails.booking.admin.views.clients

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.core.route.close
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentEnterPhoneNumberBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.clients.AuthenticateClientApi
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.factory.client.FetchAllSalonRepository
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.ui.LoginOwnerForm
import com.app.inails.booking.admin.model.ui.client.LoginForm
import com.app.inails.booking.admin.model.ui.client.UpdateProfileForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.auth.LoginRepo
import com.app.inails.booking.admin.repository.client.AuthenticateRepository
import com.app.inails.booking.admin.views.clients.dialog.RedirectToOwnerDialogOwner
import com.app.inails.booking.admin.views.clients.dialog.RedirectToOwnerDialogVn
import com.app.inails.booking.admin.views.clients.dialog.RedirectToOwnerDialogVnOwner
import com.app.inails.booking.admin.views.clients.profile.EditProfileRepository
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.youtube.player.internal.s
import okhttp3.Route

class CheckInFragment : BaseFragment(R.layout.fragment_enter_phone_number), TopBarOwner,
    RedirectToOwnerDialogOwner, RedirectToOwnerDialogVnOwner {

    private val binding by viewBinding(FragmentEnterPhoneNumberBinding::bind)
    private val viewModel by viewModel<CheckInVM>()
    private val updateInFoDialog by lazy { UserUpdateInfoDialog(appActivity) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateInFoDialog.onSubmitListener(viewModel::updateInfo, ::showConfirmDialog)
        with(binding) {
//            btnFeedback.onClick { Router.open(this@CheckInFragment, Routing.FeedBack) }
            topBar.removeView()
            viewPhoneNumber.onOkClickListener = {
                viewModel.checkIn(it)
            }
            btnLogoutOwner.onClick { logoutApp() }
            with(viewModel) {
                loginOwnerSuccess.bind{
                    Router.run { redirectToMain() }
                    success(R.string.success_redirect_owner)
                }
                showUpdateProfile.bind(updateInFoDialog::show)
                updateSuccess.bind {
                    updateInFoDialog.dismiss()
                    success(it)
                    open<ClientHomeActivity>().close()
                }
                salonName.bind {
                    binding.txtSalonName.text = it
                }
                checkInSuccess.bind {
                    success(it)

                    open<ClientHomeActivity>().close()
                }
                newMember.bind {
                    notificationDialog.apply {
                        setCancelable(false)
                        setCanceledOnTouchOutside(false)
                        show(it.first) { updateInFoDialog.show(it.second) }
                    }
                }
//                logoutSuccess.bind { open<SalonsActivity>().clear() }
                dismissUpdateSuccess.bind {
                    viewPhoneNumber.clear()
                    updateInFoDialog.dismiss()
                }
            }
        }
    }

    private fun showConfirmDialog() {
        confirmDialog.show(
            title = R.string.title_notifications,
            message = R.string.profile_dissmis_update
        ) {
            viewModel.logout()
        }
    }

    private fun logoutApp() {
        if(userLocalSource.isVietNamLanguage()){
            redirectToOwnerDialogVn.show {
                viewModel.loginOwner(it)
            }
        }else{
            redirectToOwnerDialog.show {
                viewModel.loginOwner(it)
            }
        }

    }
}

class CheckInVM(
    private val loginRepo: LoginRepository,
    private val authenticateRepository: AuthenticateRepository,
    private val salonRepository: FetchAllSalonRepository,
    private val editProfileRepo: EditProfileRepository,
    private val loginRepoOwner: LoginRepo,
    private val userLocalSource: UserLocalSource,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val salonName = SingleLiveEvent<String>()
    val updateSuccess = SingleLiveEvent<Int>()
    val dismissUpdateSuccess = SingleLiveEvent<Int>()
    val loginOwnerSuccess = SingleLiveEvent<Int>()

    init {
        launch { salonName.post(salonRepository.getSalonName()) }
    }

    val checkInSuccess = SingleLiveEvent<Int>()
    val newMember = SingleLiveEvent<Pair<Int, String>>()
    val logoutSuccess = SingleLiveEvent<Int>()
    val showUpdateProfile = SingleLiveEvent<String>()
    fun checkIn(phoneNumber: String) = launch(loading, error) {
        val user = loginRepo.checkIn(phoneNumber)
        if (user.user?.isNew!!)
            newMember.post(R.string.msg_new_member to phoneNumber)
        else if (user.user.name.isNullOrEmpty())
            showUpdateProfile.post(phoneNumber)
        else checkInSuccess.post(R.string.msg_login_success)
    }

    fun loginOwner(password: String) = launch(loading, error) {
        loginRepoOwner(LoginOwnerForm(phone = userLocalSource.getSalonPhone(), password = password))
        loginOwnerSuccess.post(R.string.msg_login_success)
    }

    fun updateInfo(form: UpdateProfileForm) = launch(loading, error) {
        editProfileRepo(form, false)
        updateSuccess.post(R.string.msg_profile_update_success)
    }

    fun logout() = launch(loading, error) {
        authenticateRepository.logout()
        dismissUpdateSuccess.call()
    }
}

@Inject(ShareScope.Fragment)
class LoginRepository(
    private val salonLocalSource: SalonLocalSource,
    private val userLocalSource: UserLocalSource,
    private val authenticateApi: AuthenticateClientApi,
    private val textFormatter: TextFormatter,
    private val appCache: AppCache
) {

//    suspend operator fun invoke(form: LoginForm): UserClientDTO {
//        form.phone = textFormatter.formatPhoneNumber(form.phone)
//        form.pushToken = appCache.clientTokenPush
//        val rs = authenticateApi.login(form).await()
//        userLocalSource.saveUserClient(rs)
//        return rs
//    }

    suspend fun checkIn(phoneNumber: String): UserClientDTO {
        val loginForm = LoginForm()
        loginForm.run {
            phone = textFormatter.formatPhoneNumber(phoneNumber)
            pushToken = appCache.deviceToken
        }
        val user = authenticateApi.checkIn(loginForm).await()
        userLocalSource.saveUserClient(user)
        return user
    }

    fun isExistSalon() = salonLocalSource.isExistSalon()
}
