package com.app.inails.booking.admin.views.auth

import android.os.Bundle
import android.os.Parcelable
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.ViewModelStateSaveAble
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityLoginBinding
import com.app.inails.booking.admin.extention.bind
import com.app.inails.booking.admin.model.ui.IAccount
import com.app.inails.booking.admin.model.ui.LoginForm
import com.app.inails.booking.admin.helper.ApplicationScope
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.auth.LoginRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Route

class LoginActivity : BaseActivity(R.layout.activity_login) {
    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val viewModel by viewModel<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            viewModel.form.run {
                etPhone.bind(::phone::set)
                etPassword.bind(::password::set)
            }
        }

        binding.btLogin.setOnClickListener {
            lifecycleScope.launch {
                viewModel.login()
            }
        }

        with(viewModel) {
            loading.bind(binding.btLogin::setEnabled) { !this }
            loginSuccess.bind { Router.run { redirectToMain() } }
        }
    }
}

class LoginViewModel(
    private val loginRepo: LoginRepo
) :ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    var form: LoginForm = LoginForm()
        private set
    val loginSuccess = SingleLiveEvent<Int>()

    fun login() = launch(loading, error) {
        loginRepo(form)
        delay(1000)
        loginSuccess.post(R.string.msg_login_success)
    }

}