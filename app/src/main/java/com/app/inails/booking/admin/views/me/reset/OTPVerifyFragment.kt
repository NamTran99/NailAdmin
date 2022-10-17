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
import com.app.inails.booking.admin.app.AppConfig
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentOtpVerifyBinding
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.extention.lockButton
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.coroutines.delay


class OTPVerifyFragment : BaseFragment(R.layout.fragment_otp_verify), TopBarOwner {

    private val binding by viewBinding(FragmentOtpVerifyBinding::bind)
    private val viewModel by viewModel<OTPVerifyVM>()
    private val arg by lazyArgument<Routing.OTPVerify>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                title = R.string.title_reset_password,
                onBackClick = { activity?.onBackPressed() })
        )
        with(binding) {
            viewOTP.onActiveChangeListener = { it lockButton btnResetPassword }
            btnResetPassword.onClick {
                viewModel.verifyOTP(viewOTP.otp)
            }
            viewOTP.requestFocusOTP()

            with(viewModel) {
                success.bind { Router.navigate(self, Routing.ResetPasswordSuccess(it)) }
                errorCustom.bind { viewOTP.setError(it.message) }
                loadingCustom.bind { viewOTP.showLoading(it) }
                resendCounting.bind(ResendOTPAdapter(binding) {
                    viewModel.setResending(arg.phoneNumber)
                })
                countToRequestResend()
            }
        }
    }
}

class OTPVerifyVM(
    private val verifyOTPRepo: VerifyOTPRepo,
    private val requestOTPRepo: RequestOTPRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {


    val resendCounting = MutableLiveData<ResendState>(ResendState.Initialize)

    val errorCustom: ErrorEvent = ErrorLiveData()
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val success = verifyOTPRepo.result

    fun verifyOTP(otp: String?) = launch(loadingCustom, errorCustom) {
        verifyOTPRepo(otp)
    }

    fun setResending(phoneNumber: String) = launch(loading, error) {
        resendCounting.post(ResendState.Sending)
        val exception = runCatching { requestOTPRepo(phoneNumber) }
            .exceptionOrNull()
        countToRequestResend()
        if (exception != null) throw exception
    }

    fun countToRequestResend() = launch {
        val timeout = AppConfig.otpTimeout
        var remain = timeout
        val counting = ResendState.Counting(remain.toString())
        while (remain > 0) {
            resendCounting.post(counting.copy(remain = remain.toString()))
            remain -= 1
            delay(1000)
        }
        resendCounting.post(ResendState.ReadyToResend)
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

sealed class ResendState {
    object Initialize : ResendState()
    data class Counting(val remain: String) : ResendState()
    object ReadyToResend : ResendState()
    object Sending : ResendState()
}
