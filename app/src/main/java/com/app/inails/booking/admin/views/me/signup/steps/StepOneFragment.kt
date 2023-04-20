package com.app.inails.booking.admin.views.me.signup.steps

import android.os.Bundle
import android.support.core.view.viewBinding
import android.support.viewmodel.shareViewModel
import android.util.Log
import android.view.View
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentStepOneBinding
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.extention.getTextString
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.SignUpForm
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.me.signup.SignUpVM
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class StepOneFragment : BaseFragment(R.layout.fragment_step_one), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by shareViewModel<SignUpVM>()
    val binding by viewBinding(FragmentStepOneBinding::bind)
    val signUpForm: SignUpForm
        get() = viewModel.salonForm

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up,
                onBackClick = {
                    confirmDialog.show(
                        title = getString(R.string.notice),
                        message = getString(R.string.message_exit),
                        function = {
                            activity?.onBackPressed()
                        }
                    )
                },
            )
        )
        binding.apply {
            edtAccPhone.inputTypePhoneUS()
            btnContinue.onClick {
                signUpForm.run {
                    phone = edtAccPhone.getTextString().convertPhoneToNormalFormat()
                    admin_name = edtAccName.text.toString()
                    password = edtAccPassword.getTextString()
                }
                viewModel.checkValidStep1()
            }
        }
    }
}
