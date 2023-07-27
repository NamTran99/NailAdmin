package com.app.inails.booking.admin.views.me.reset

import androidx.lifecycle.Observer
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.FragmentOtpVerifyBinding
import com.app.inails.booking.admin.extention.gone
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show

class ResendOTPAdapter (
    private val binding: FragmentOtpVerifyBinding,
    private val onResendListener: () -> Unit
) : Observer<ResendState> {

    override fun onChanged(t: ResendState) {
        val txtResend = binding.txtOTPResend
        when (t) {
            ResendState.Initialize -> txtResend.gone()
            ResendState.ReadyToResend -> {
                txtResend.show {
                    text = getString(R.string.auth_otp_resend)
                }
                txtResend.onClick {
                    onResendListener()
                }
                txtResend.isActivated = true
                txtResend.isEnabled = true
            }
            is ResendState.Counting -> {
                txtResend.text =
                    getString(R.string.auth_otp_resend_in_remaining_s, t.remain)
                txtResend.onClick(null)
                txtResend.isActivated = false
                txtResend.isEnabled = false
            }
            is ResendState.Sending -> {
                txtResend.text = getString(R.string.auth_otp_sending)
                txtResend.onClick(null)
                txtResend.isActivated = false
                txtResend.isEnabled = false
            }
        }
    }

    private fun getString(resId: Int, vararg args: String): String {
        return binding.root.resources.getString(resId, *args)
    }
}