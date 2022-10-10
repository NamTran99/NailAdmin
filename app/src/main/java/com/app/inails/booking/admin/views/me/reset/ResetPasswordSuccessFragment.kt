package com.app.inails.booking.admin.views.me.reset

import android.os.Bundle
import android.support.core.route.close
import android.support.core.route.lazyArgument
import android.support.core.view.viewBinding
import android.view.View
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentResetPwSuccessBinding
import com.app.inails.booking.admin.extention.copyText
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToLogin
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ResetPasswordSuccessFragment : BaseFragment(R.layout.fragment_reset_pw_success),
    TopBarOwner {

    private val binding by viewBinding(FragmentResetPwSuccessBinding::bind)
    private val arg by lazyArgument<Routing.ResetPasswordSuccess>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                title = R.string.title_reset_password,
                onBackClick = { activity?.onBackPressed() })
        )
        with(binding) {
            txtNewPassword.text = arg.newPassword
            btnToLogin.onClick {  Router.run { redirectToLogin() } }
            btnClose.onClick {  Router.run { redirectToLogin() } }
            btnCopy.onClick { copyText(arg.newPassword) }
        }
    }
}
