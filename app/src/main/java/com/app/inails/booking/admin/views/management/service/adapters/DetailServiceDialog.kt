package com.app.inails.booking.admin.views.management.service.adapters

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogDetailServiceBinding
import com.app.inails.booking.admin.databinding.DialogMessageBinding
import com.app.inails.booking.admin.extention.displaySafe
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.dialog.ConfirmNoticeDialog

class DetailServiceDialog(context: Context) : BaseDialog(context) {
    private var mOnDismiss: () -> Unit = {}
    private val binding = viewBinding(DialogDetailServiceBinding::inflate)

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(service: IService) {
        with(binding){
            tvSvName.text = service.name.displaySafe()
            tvSvPrice.text = service.price.formatPrice().displaySafe()
        }
        super.show()
    }
}

interface DetailServiceDialogOwner : ViewScopeOwner {
    val detailServiceDialog: DetailServiceDialog
        get() = with(viewScope) {
            getOr("detailService:dialog") { DetailServiceDialog(context) }
        }
}