package com.app.inails.booking.admin.views.auth

import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.*
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityLoginBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.showPopUp
import com.app.inails.booking.admin.model.ui.LoginOwnerForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LoginRepo
import com.app.inails.booking.admin.utils.Utils
import com.app.inails.booking.admin.views.main.MainActivity
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class OptionLogin(var isEnableBackToPre: Boolean = false) : BundleArgument

class LoginActivity : BaseActivity(R.layout.activity_login) {
    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val viewModel by viewModel<LoginViewModel>()
    private val argument by lazy { nullableArguments<OptionLogin>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            etPhone.inputTypePhoneUS()
            tvResetPassword.onClick {
                Router.open(this@LoginActivity, Routing.ResetPassword)
            }
            btSignUpOwner.onClick {
                Router.open(this@LoginActivity, Routing.SignUpGeneral)
            }
            btSignUpMani.onClick{
                Router.open(this@LoginActivity, Routing.SignUpMani)
            }
            btSupport.onClick {
                Router.open(this@LoginActivity, Routing.ContactAccount)
            }
            btLoginOwner.onClick{
                viewModel.form.run {
                    phone = binding.etPhone.text.toString()
                    password = binding.etPassword.text.toString()
                }
                viewModel.login(LoginRepo.LoginType.Owner)
            }
            btLoginClient.onClick{
                viewModel.form.run {
                    phone = binding.etPhone.text.toString()
                    password = binding.etPassword.text.toString()
                }
                viewModel.login(LoginRepo.LoginType.Manicurist)
            }
            switchLan.showPopUp(R.menu.menu_language) { id ->
                val lan = when (id) {
                    R.id.lanVi -> "vi"
                    R.id.lanEn -> "en"
                    else -> "vi"
                }
                setLanguage(lan)
            }

            switchLan.setText(if (viewModel.isVnLanguage) R.string.vietnamese_1 else R.string.english)
            binding.tvVersion.text = Utils.getDisplayBuildConfig(
                this@LoginActivity,
                userLocalSource.getLanguageWithDefault()
            )
        }

//        binding.btLogin.setOnClickListener {
//            lifecycleScope.launch {
//                viewModel.form.run {
//                    phone = binding.etPhone.text.toString()
//                    password = binding.etPassword.text.toString()
//                }
//                viewModel.login()
//            }
//        }

        with(viewModel) {
//            loading.bind(binding.btLogin::setEnabled) { !this }
            loginSuccess.bind {
                if (argument != null) {
                    close()
                } else {
                    open<MainActivity>().clear()
                }
            }
        }
    }

    override fun onResume() {
        binding.switchLan.setText(if (viewModel.isVnLanguage) R.string.vietnamese_1 else R.string.english)
        super.onResume()
    }
}

class LoginViewModel(
    private val loginRepo: LoginRepo,
    private val userLocalSource: UserLocalSource
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    var form: LoginOwnerForm = LoginOwnerForm()
        private set
    val loginSuccess = SingleLiveEvent<Int>()
    val isVnLanguage = userLocalSource.getLanguage() == "vi"

    fun login(type: LoginRepo.LoginType) = launch(loading, error) {
        loginRepo(form, type)
        loginSuccess.post(R.string.msg_login_success)
    }
}