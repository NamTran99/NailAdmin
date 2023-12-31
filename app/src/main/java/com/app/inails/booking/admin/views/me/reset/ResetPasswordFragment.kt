package com.app.inails.booking.admin.views.me.reset

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentResetPasswordBinding
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ResetPasswordFragment : BaseFragment(R.layout.fragment_reset_password), TopBarOwner {
    val viewModel by viewModel<ResetPasswordViewModel>()
    val binding by viewBinding(FragmentResetPasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_reset_password,
                onBackClick = {
                    activity?.onBackPressed()
                },
            ))

        with(binding) {
            edtPhoneNumber.inputTypePhoneUS()
            btnSubmit.onClick{
                viewModel.submit(edtPhoneNumber.text.toString())
            }
            grRole.setOnCheckedChangeListener { _, checkedId ->
                viewModel.role = when(checkedId){
                    rbOwner.id -> 2
                    rbMani.id -> 3
                    else -> 2
                }
                Log.d("TAG", "onViewCreated: namtd8 ${viewModel.role}")
            }
        }

        viewModel.redirectToVerifyOTP.bind {
            Router.open(
                self,
                Routing.OTPVerify(binding.edtPhoneNumber.text.toString())
            )
        }

    }
}

class ResetPasswordViewModel(
    private val requestOTPRepo: RequestOTPRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val redirectToVerifyOTP = SingleLiveEvent<Any>()
    var role : Int = -1 // 2 owner, 3 mani

    fun submit(phoneNumber: String) = launch(loading, error) {
        if (phoneNumber.isBlank()) viewError(
            R.id.edtPhoneNumber,
            R.string.error_blank_phone
        )
        if (role == -1){
            resourceError(R.string.error_pls_select_role)
        }
        redirectToVerifyOTP.post(requestOTPRepo(phoneNumber, role))
    }
}

@Inject(ShareScope.Fragment)
class RequestOTPRepo(
    private val authenticateApi: AuthenticateApi,
    private val textFormatter: TextFormatter,
) {

    suspend operator fun invoke(phoneNumber: String, role: Int = 2) {
        authenticateApi.requestOTP(textFormatter.formatPhoneNumber(phoneNumber), role.toString()).await()
    }

}
