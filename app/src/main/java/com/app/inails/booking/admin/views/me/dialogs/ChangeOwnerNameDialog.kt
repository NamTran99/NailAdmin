package com.app.inails.booking.admin.views.me.dialogs

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogChangeOwnerNameBinding
import com.app.inails.booking.admin.databinding.DialogEmailReceiveFeedbackBinding
import com.app.inails.booking.admin.databinding.DialogSignUpSuccessBinding
import com.app.inails.booking.admin.extention.displayErrorAndFocus
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.me.adapters.CreateUpdateEmailDialog

class ChangeOwnerNameDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogChangeOwnerNameBinding::inflate)

    init {
        setCancelable(false)
        binding.btnClose.onClick {
            confirmDialog.show(
                title = context.getString(R.string.change_owner_name),
                message = context.getString(R.string.message_exit),
                function = {
                    dismiss()
                }
            )
        }
    }

    fun show(
        oldName: String,
        function: (String) -> Unit
    ) {
        with(binding) {
            etOwnerName.setText(oldName)
            btSave.setOnClickListener {
                etOwnerName.text.toString().apply{
                    if(this.isBlank()){
                        etOwnerName.displayErrorAndFocus(R.string.error_blank_owner_name)
                    }else{
                        function.invoke(etOwnerName.text.toString())
                        dismiss()
                    }
                }
            }
        }
        super.show()
    }
}

interface ChangeOwnerNameDialogOwner : ViewScopeOwner {
    val changeOwnerNameDialog: ChangeOwnerNameDialog
        get() = with(viewScope) {
            getOr("ChangeOwnerName:dialog") { ChangeOwnerNameDialog(context) }
        }
}