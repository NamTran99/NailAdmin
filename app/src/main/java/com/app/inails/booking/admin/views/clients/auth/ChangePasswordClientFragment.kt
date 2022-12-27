package com.app.inails.booking.admin.views.clients.auth

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentChangePasswordBinding
import com.app.inails.booking.admin.databinding.FragmentChangePasswordClientBinding
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.datasource.remote.clients.AuthenticateClientApi
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.form.ChangePasswordForm
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ChangePasswordClientFragment : BaseFragment(R.layout.fragment_change_password_client), TopBarOwner {

    private val binding by viewBinding(FragmentChangePasswordClientBinding::bind)
    private val viewModel by viewModel<ChangePasswordVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_change_password) { findNavigator().navigateUp() })
        binding.btnSave.onClick { submit() }
        viewModel.success.bind {
            success(it)
            findNavigator().navigateUp()
        }
    }

    private fun submit() = with(binding) {
        viewModel.form.run {
            oldPassword = edtOldPassword.text.toString()
            newPassword = edtNewPassword.text.toString()
            confirmPassword = edtConfirmPassword.text.toString()
        }
        viewModel.submit()
    }
}

class ChangePasswordVM(
    private val changePasswordRepo: ChangePasswordRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    var form = ChangePasswordForm()
    val success = SingleLiveEvent<Int>()

    fun submit() = launch(loading, error) {
        form.validate()
        changePasswordRepo(form)
        success.post(R.string.msg_change_password_success)
    }
}

@Inject(ShareScope.Fragment)
class ChangePasswordRepo(
    private val authenticateApi: AuthenticateClientApi
) {

    suspend operator fun invoke(form: ChangePasswordForm) {
        authenticateApi.changePassword(form).await()
    }
}
