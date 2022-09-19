package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.text.InputFilter
import android.widget.Toast
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogFinishBookingBinding
import com.app.inails.booking.admin.extention.formatAmount
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.views.widget.DecimalDigitsInputFilter


class FinishBookingDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFinishBookingBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        apm: IAppointment,
        function: (Double, String) -> Unit
    ) {
        with(binding) {
            (ServicePriceAdapter(rvServices)).apply {
                submit(apm.serviceList)
            }
            etNote.setText("")
            etAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(11, 2))
            etAmount.setText(apm.totalPrice.formatAmount())
            btSubmit.onClick {
                val amount = etAmount.text.toString().toDoubleOrNull()
                if (amount == null) {
                    Toast.makeText(context, R.string.error_empty_amount, Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                if (amount == 0.0) {
                    Toast.makeText(
                        context,
                        R.string.error_empty_amount_greater_than,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@onClick
                }
                function.invoke(amount, etNote.text.toString())
            }
        }
        super.show()
    }

}

interface FinishBookingOwner : ViewScopeOwner {
    val finishBookingDialog: FinishBookingDialog
        get() = with(viewScope) {
            getOr("finish_booking:dialog") { FinishBookingDialog(context) }
        }
}