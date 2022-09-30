package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCustomerInfoBinding
import com.app.inails.booking.admin.extention.displaySafe
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ICustomer


class CustomerInfoDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogCustomerInfoBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        customer: ICustomer
    ) {
        with(binding) {
            tvStaffName.text = customer.name.displaySafe()
            tvPhone.text = customer.phone.displaySafe()
            tvAddress.text = customer.address.displaySafe()
            tvEmail.text = customer.email.displaySafe()
        }
        super.show()
    }

}

interface CustomerInfoOwner : ViewScopeOwner {
    val customerInfoDialog: CustomerInfoDialog
        get() = with(viewScope) {
            getOr("customer_info:dialog") { CustomerInfoDialog(context) }
        }
}