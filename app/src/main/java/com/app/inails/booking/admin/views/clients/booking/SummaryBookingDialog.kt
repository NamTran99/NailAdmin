package com.app.inails.booking.admin.views.clients.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import androidx.core.widget.addTextChangedListener
import com.app.inails.booking.admin.DataConst.VoucherType.TYPE_PERCENT
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogSummaryBookingBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.client.IServiceClient
import com.app.inails.booking.admin.model.ui.client.IVoucherClient


class SummaryBookingDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogSummaryBookingBinding::inflate)

    private var mVoucher: IVoucherClient? = null

    init {

        binding.edtVoucher.addTextChangedListener {
            binding.btnApplyVoucher.isEnabled = it!!.isNotEmpty()
        }

        binding.btnBack.onClick {
            dismiss()
        }

        binding.btnDelete.onClick {
            binding.applyVoucherLayout.hide()
            mVoucher = null
            binding.edtVoucher.isEnabled = true
            binding.edtVoucher.setText("")
            binding.btnApplyVoucher.show()
            binding.btnDelete.hide()
            binding.btnInfo.hide()
        }
    }

    fun show(
        staffName: String?,
        dateTime: String,
        services: List<IServiceClient>,
        onSubmit: (String?) -> Unit = {},
        onApplyVoucher: (String, Float) -> Unit,
        onVoucherInfo: (String) -> Unit
    ) = with(binding) {
        mVoucher = null
        txtRsStaff.text = staffName ?: context.getString(R.string.label_anyone_available)
        edtVoucher.setText("")
        txtDiscount.text = ""
        ServiceSummaryAdapter(rcvService).submit(services)
        txtTotal.text = String.format("$%.2f", totalPrice(services)).replace(",", ".")
        txtTotalAmount.text = String.format("$%.2f", totalPrice(services)).replace(",", ".")
        btnSubmit.onClick {
            dismiss()
            onSubmit.invoke(mVoucher?.code)
        }

        btnApplyVoucher.onClick {
            onApplyVoucher.invoke(edtVoucher.text.toString(), totalPrice(services))
        }
        applyVoucherLayout.hide()
        edtVoucher.isEnabled = true
        btnDelete.hide()
        btnApplyVoucher.show()
        btnInfo.hide()

        binding.btnInfo.onClick {
            mVoucher?.let {
                val message =
                    "${it.description}\nStart date: ${it.startDate}\nExpiration date: ${it.expirationDate}"
                onVoucherInfo.invoke(message)
            }
        }
        super.show()
    }

    fun updateVoucher(voucher: IVoucherClient) = with(binding) {
        mVoucher = voucher
        txtPriceDiscount.text = voucher.discount
        if (voucher.type == TYPE_PERCENT) {
            txtDiscount.text = voucher.percent
        }
        txtTotalAmount.text = voucher.totalAmount
        edtVoucher.isEnabled = false
        btnDelete.show()
        btnApplyVoucher.hide()
        applyVoucherLayout.show()
        btnInfo.show()
    }

    fun totalPrice(services: List<IServiceClient>): Float {
        var total = 0f
        services.forEach {
            total += it.priceF
        }
        return total
    }
}

interface SummaryBookingDialogOwner : ViewScopeOwner {
    val summaryBookingDialog: SummaryBookingDialog
        get() = with(viewScope) {
            getOr("summary_booking:dialog") { SummaryBookingDialog(context) }
        }
}