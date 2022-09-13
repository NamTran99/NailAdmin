package com.app.inails.booking.admin.views.me

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
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentChangePasswordBinding
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.model.ui.UpdateUserPasswordForm
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ChangePasswordFragment : BaseFragment(R.layout.fragment_change_password), TopBarOwner {
    val viewModel by viewModel<ChangePasswordViewModel>()
    val binding by viewBinding(FragmentChangePasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_change_password
            ) { activity?.onBackPressed() })

        with(binding) {
            btUpdatePassword.setOnClickListener {
                viewModel.form.run {
                    oldPassword = edtOldPassword.text
                    newPassword = edtNewPassword.text
                    confirmPassword = edtConfirmPassword.text
                }
                viewModel.submit()
            }
        }

        with(viewModel){
            success.bind {
                success("Success")
                activity?.onBackPressed()
            }
        }
    }
}

class ChangePasswordViewModel(
    private val changePasswordRepository: ChangePasswordRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val success = SingleLiveEvent<Any>()
    val form = UpdateUserPasswordForm()

    fun submit() = launch(loading, error) {
        success.post(changePasswordRepository(form))
    }
}

@Inject(ShareScope.Fragment)
class ChangePasswordRepository(
    private val meApi: MeApi
) {
    suspend operator fun invoke(form: UpdateUserPasswordForm) {
        form.validate()
        meApi.changePassword(form).await()
    }
}