package com.app.inails.booking.admin.views.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogNoticeConfirmBinding
import com.app.inails.booking.admin.extention.onClick


class ConfirmNoticeDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogNoticeConfirmBinding::inflate)

    init {
        binding.btnCancel.onClick {
            dismiss()
        }
    }

    fun show(
        title: String,
        message: String,
        function: () -> Unit = {}
    ) {
        binding.txtTitle.text = title
        binding.txtBody.text = message
        binding.btnDismiss.onClick {
            function()
            dismiss()
        }
        super.show()
    }

    fun show(
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes buttonConfirm: Int = R.string.btn_confirm,
        function: () -> Unit = {}
    ) {
        binding.txtTitle.setText(title)
        binding.txtBody.setText(message)
        binding.btnDismiss.setText(buttonConfirm)
        binding.btnDismiss.onClick {
            function()
            dismiss()
        }
        super.show()
    }
}

interface ConfirmDialogOwner : ViewScopeOwner {
    val confirmDialog: ConfirmNoticeDialog
        get() = with(viewScope) {
            getOr("confirm:dialog") { ConfirmNoticeDialog(context) }
        }


}