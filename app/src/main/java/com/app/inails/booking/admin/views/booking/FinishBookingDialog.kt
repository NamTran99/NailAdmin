package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.core.view.ViewScopeOwner
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.DialogFinishBookingBinding
import com.app.inails.booking.admin.extention.formatAmount
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.extention.toPriceValue
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.widget.DecimalDigitsInputFilter
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter


class FinishBookingDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFinishBookingBinding::inflate)
    private lateinit var beforeImageAdapter: UploadPhotoAdapter
    private lateinit var afterImageAdapter: UploadPhotoAdapter
    init {
        binding.apply {
            btClose.onClick {
                dismiss()
            }

            beforeImageAdapter = UploadPhotoAdapter(rcBeforePhoto).apply {
                onAddImagesAction = {
                    onAddBeforeImage.invoke()
                }
                onRemoveImageAction = {
                    onclickRemoveBeforeImage.invoke(it)
                }
            }
            afterImageAdapter = UploadPhotoAdapter(rcAfterphoto).apply {
                onAddImagesAction = {
                    onAddAfterImage.invoke()
                }
                onRemoveImageAction = {
                    onclickRemoveAfterImage.invoke(it)
                }
            }
        }
    }

    var onAddBeforeImage :(() -> Unit) = {}
    var onAddAfterImage :(() -> Unit) = {}
    var onclickRemoveBeforeImage :((AppImage) -> Unit) = {}
    var onclickRemoveAfterImage :((AppImage) -> Unit) = {}

    fun updateBeforeImages(images: ArrayList<AppImage>){
        beforeImageAdapter.changePath(images)
    }

    fun updateAfterImages(images: ArrayList<AppImage>){
        afterImageAdapter.changePath(images)
    }

    fun show(
        apm: IAppointment,
        function: (Double, String) -> Unit
    ) {
        with(binding) {
            txtDiscount.show(apm.showPercent)
            txtDiscount.text = apm.percent
            txtPriceDiscount.text = apm.discount
            txtTotal.text = apm.totalPriceService
            etAmount.setText(apm.totalAmount.toPriceValue())
            voucherLayout.show(apm.hasVoucher)
            val list = apm.serviceList.toMutableList()
            apm.serviceCustomObj?.let { list.add(it) }
            (ServicePriceAdapter(rvServices)).apply {
                submit(list)
            }
            etNote.setText("")
            etAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(11, 2))
//            etAmount.setText(apm.totalPrice.formatAmount())
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