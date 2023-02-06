package com.app.inails.booking.admin.views.booking.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogVoucherDetailBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.setTextViewDrawableColor
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IVoucher
import com.app.inails.booking.admin.model.ui.VoucherType
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner


class VoucherDetailDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogVoucherDetailBinding::inflate)

    var onClickDeleteVoucher :((id: IVoucher) -> Unit) = {}
    init {
        setCancelable(false)
        binding.apply {

            btClose.setOnClickListener {
                dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun show(
        item: IVoucher,
        isEnableDeleteVoucher:Boolean = false
    ) {
        with(binding) {
            btnDelete.show(isEnableDeleteVoucher)
            btnDelete.onClick{
                onClickDeleteVoucher.invoke(item)
                dismiss()
            }
            tvCode.text = item.code
            tvCustomerType.setText(item.typeCustomer)
            if(item.typeName == VoucherType.PERCENT){
                tvValue.text = "-${item.valueDiscount}%"
            }else{
                tvValue.text = "-${item.valueDiscount}$"
            }
            tvStartTime.text = item.startDate
            tvEndTime.text = item.endDate
            tvDescription.text = item.description
            lvDescription.show(item.description.isNotBlank())
            tvStatus.setText(item.statusName)
            tvStatus.setTextColor(context.getColor(item.statusColor))
            tvStatus.setTextViewDrawableColor(item.statusColor)
            tvInform.show(item.status == 4)
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