package com.app.inails.booking.admin.views.me.reset

import android.os.Bundle
import android.support.core.event.ErrorEvent
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.ErrorLiveData
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.post
import android.support.core.route.lazyArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentOtpVerifyBinding
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner


class OTPVerifyFragment : BaseFragment(R.layout.fragment_otp_verify), TopBarOwner {

    private val binding by viewBinding(FragmentOtpVerifyBinding::bind)
    private val viewModel by viewModel<OTPVerifyVM>()
    private val arg by lazyArgument<Routing.ResetPassword>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                title = R.string.title_reset_password,
                onBackClick = { activity?.onBackPressed() })
        )
        with(binding) {
            viewOTP.onActiveChangeListener = {
                btnResetPassword.isEnabled = it
            }
            btnResetPassword.onClick {
                viewModel.verifyOTP(viewOTP.otp)
            }

            with(viewModel) {
                success.bind { Router.open(self, Routing.ResetPasswordSuccess(it)) }
                errorCustom.bind { viewOTP.setError(it.message) }
                loadingCustom.bind { viewOTP.showLoading(it) }
            }
        }
    }
}

class OTPVerifyVM(
    private val verifyOTPRepo: VerifyOTPRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val errorCustom: ErrorEvent = ErrorLiveData()
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val success = verifyOTPRepo.result

    fun verifyOTP(otp: String?) = launch(loadingCustom, errorCustom) {
        verifyOTPRepo(otp)
    }

}

@Inject(ShareScope.Fragment)
class VerifyOTPRepo(private val authenticateApi: AuthenticateApi) {
    val result = MutableLiveData<String>()

    suspend operator fun invoke(otp: String?) {
        if (otp == null) return
        result.post(authenticateApi.verifyOTP(otp).await().newPassword)
    }
}
