package com.app.inails.booking.admin.views.auth

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.clear
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityLoginBinding
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.model.ui.LoginForm
import com.app.inails.booking.admin.repository.auth.LoginRepo
import com.app.inails.booking.admin.views.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity(R.layout.activity_login) {
    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val viewModel by viewModel<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            etPhone.inputTypePhoneUS()
        }

        binding.btLogin.setOnClickListener {
            lifecycleScope.launch {
                viewModel.form.run {
                    phone = binding.etPhone.text.toString()
                    password = binding.etPassword.text.toString()
                }
                viewModel.login()
            }
        }

        with(viewModel) {
            loading.bind(binding.btLogin::setEnabled) { !this }
            loginSuccess.bind { open<MainActivity>().clear() }
        }
    }
}

class LoginViewModel(
    private val loginRepo: LoginRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    var form: LoginForm = LoginForm()
        private set
    val loginSuccess = SingleLiveEvent<Int>()

    fun login() = launch(loading, error) {
        loginRepo(form)
        loginSuccess.post(R.string.msg_login_success)
    }

}