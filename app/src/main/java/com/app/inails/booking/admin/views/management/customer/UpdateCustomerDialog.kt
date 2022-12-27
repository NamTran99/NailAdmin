package com.app.inails.booking.admin.views.management.customer

import android.content.Context
import android.os.Build
import android.support.core.view.ViewScopeOwner
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogUpdateCustomerBinding
import com.app.inails.booking.admin.extention.displaySafe
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner


class UpdateCustomerDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogUpdateCustomerBinding::inflate)
    private var customerType = 2 //2 normal, 3, vip
        set(value) {
            setUINormalType(value == 2)
            field = value
        }

    init {
        setCancelable(false)
        binding.apply {
            rbGroupType.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    rbNormal.id -> customerType = 2
                    rbVip.id -> customerType = 3
                }
            }
            btClose.onClick{
                confirmDialog.show(
                    title = context.getString(R.string.tittle_exit_update_customer),
                    message = context.getString(R.string.message_exit),
                    function = {
                        dismiss()
                    }
                )
            }
        }
    }

    private fun setUINormalType(isNormal: Boolean) {
        binding.apply {
            if (isNormal) {
                rbNormal.setTextColor(context.getColor(R.color.white))
                rbVip.setTextColor(context.getColor(R.color.colorPrimary))
            } else {
                rbNormal.setTextColor(context.getColor(R.color.colorPrimary))
                rbVip.setTextColor(context.getColor(R.color.white))
            }
        }
    }

    fun show(
        customer: ICustomer? = null,
        function: ((id:Int,note: String,type: Int) -> Unit)?= null
    ) {
        with(binding) {
            customer?.let{ customer ->
                customerType = customer.type
                rbGroupType.check(if(customer.type ==2)rbNormal.id else rbVip.id)
                etNote.setText(customer.note)

                etName.setText(customer.name.displaySafe())
                etPhone.setText(customer.phone.displaySafe())
                etBirthday.setText(customer.birthDay.displaySafe())
                etEmail.setText(customer.email.displaySafe())
                etLocation.setText(customer.address.displaySafe())
                btSubmit.onClick{
                    function?.invoke(customer.id, etNote.text.toString(), customerType)
                    dismiss()
                }
            }

            rbGroupType.setOnCheckedChangeListener { radioGroup, i ->
                customerType = when (i) {
                    rbNormal.id -> {
                        2
                    }
                    rbVip.id -> {
                        3
                    }
                    else -> {
                        2
                    }
                }
            }
        }
        super.show()
    }
}

interface UpdateCustomerDialogOwner : ViewScopeOwner {
    val updateCustomerDialog: UpdateCustomerDialog
        get() = with(viewScope) {
            getOr("staff:dialog") { UpdateCustomerDialog(context) }
        }
}