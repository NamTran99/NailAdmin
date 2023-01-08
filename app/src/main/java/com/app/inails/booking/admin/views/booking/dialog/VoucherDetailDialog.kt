package com.app.inails.booking.admin.views.booking.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogVoucherDetailBinding
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IVoucher
import com.app.inails.booking.admin.model.ui.VoucherType
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.me.adapters.CreateUpdateEmailDialog


class VoucherDetailDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogVoucherDetailBinding::inflate)

    init {
        setCancelable(false)
        binding.btClose.setOnClickListener {
            dismiss()
        }
    }

    fun show(
        item: IVoucher
    ) {
        with(binding) {
            tvCode.text = item.code
            tvCustomerType.setText(item.typeCustomer)
            if(item.type == VoucherType.PERCENT){
                tvValue.text = "-${item.value}%"
            }else{
                tvValue.text = "-${item.value}$"
            }
            tvStartTime.text = item.startDate
            tvEndTime.text = item.endDate
            tvDescription.text = item.description
            lvDescription.show(item.description.isNotBlank())
        }
        super.show()
    }
}

interface VoucherDetailDialogOwner : ViewScopeOwner {
    val voucherDetailDialog: VoucherDetailDialog
        get() = with(viewScope) {
            getOr("voucherDetailDialog:dialog") { VoucherDetailDialog(context) }
        }
}