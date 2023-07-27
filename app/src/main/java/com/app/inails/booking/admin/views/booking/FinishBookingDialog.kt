package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.graphics.Rect
import android.support.core.view.ViewScopeOwner
import android.view.MotionEvent
import android.widget.EditText
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.base.LinearSpacingItemDecoration
import com.app.inails.booking.admin.databinding.DialogFinishBookingBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.sangcomz.fishbun.util.getDimension


class FinishBookingDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFinishBookingBinding::inflate)
    private lateinit var beforeImageAdapter: UploadPhotoAdapter
    private lateinit var afterImageAdapter: UploadPhotoAdapter
    private lateinit var moreServiceAdapter: ServicePriceAdapter
    private lateinit var searchServiceAdapter: ServicePriceAdapter
    lateinit var currentApmInfor: IAppointment

    var discount: Double
        get() = currentApmInfor.discount
    set(value){
        binding.txtPriceDiscount.text = "-${value.formatPrice()}"
    }

    var totalAmount: Double
        get() = currentApmInfor.totalAmount
        set(value) {
            binding.apply {
                if (value < 0) {
                    tvDola.hide()
                    etAmount.hide()
                    tvFree.show()
                } else {
                    tvFree.hide()
                    tvDola.show()
                    etAmount.show()
                    etAmount.text = value.display()
                }
            }
        }

    var addServiceMode: Boolean = false
        set(value) {
            binding.apply {
                etName.show(value)
                etPrice.show(value)
                btAdd.show(value)
                btCancel.show(value)
                btAddService.hide(value)
                lvSearch.hide()
                etName.showKeyboard(false)
                etName.clearFocus()
                etPrice.clearFocus()
            }
            field = value
        }

    val listMoreService: MutableList<IService> = mutableListOf()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.lvSearch.isShown) {
            binding.lvSearch.hide()
        } else {
            this.dismiss()
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEventCustom(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                val searchRect = Rect()
                binding.lvSearch.getGlobalVisibleRect(searchRect)
                if (!outRect.contains(event.x.toInt(), event.y.toInt()) && !searchRect.contains(
                        event.x.toInt(),
                        event.y.toInt()
                    )
                ) {
                    v.clearFocus()
                    v.showKeyboard(false)
                    binding.lvSearch.hide()
                }
            }
        }
    }

    init {
        addServiceMode = false
        binding.apply {
            etPrice.setInputSignedDecimal()
            etName.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    onSearchService.invoke(etName.text.toString())
                    lvNest.smoothScrollTo(0, btCancel.top)
                }
            }
            btAddService.setOnClickListener {
                addServiceMode = true
            }
            btCancel.setOnClickListener {
                addServiceMode = false
            }

            btAdd.onClick {
                if (checkValidate()) {
                    listMoreService.add(
                        MoreServiceForm(
                            etName.text.toString(),
                            etPrice.text.toString().trim().toDouble()
                        )
                    )
                    etName.clearText()
                    etPrice.clearText()
                    lvNest.smoothScrollTo(0, 0)
                    moreServiceAdapter.submit(listMoreService)
                    updateTotalValue()
                }
            }

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
                rcBeforePhoto.addItemDecoration(
                    LinearSpacingItemDecoration(context.getDimension(R.dimen.size_10))
                )
            }
            afterImageAdapter = UploadPhotoAdapter(rcAfterphoto).apply {
                onAddImagesAction = {
                    onAddAfterImage.invoke()
                }
                onRemoveImageAction = {
                    onclickRemoveAfterImage.invoke(it)
                }
                rcAfterphoto.addItemDecoration(
                    LinearSpacingItemDecoration(context.getDimension(R.dimen.size_10))
                )
            }

            moreServiceAdapter = ServicePriceAdapter(rvMoreServices, true).apply {
                onClickRemoveItem = {
                    listMoreService.remove(it)
                    moreServiceAdapter.submit(listMoreService)
                    updateTotalValue()
                }
            }
            searchServiceAdapter = ServicePriceAdapter(rvSearchService).apply {
                onItemClick = {
                    addServiceMode = true
                    etName.setText(it.name)
                    etPrice.setText(it.price.toString())
                }
            }
        }
    }

    var onAddBeforeImage: (() -> Unit) = {}
    var onAddAfterImage: (() -> Unit) = {}
    var onclickRemoveBeforeImage: ((AppImage) -> Unit) = {}
    var onclickRemoveAfterImage: ((AppImage) -> Unit) = {}
    var onSearchService: ((String) -> Unit) = {}

    fun updateBeforeImages(images: ArrayList<AppImage>) {
        beforeImageAdapter.changePath(images)
    }

    fun updateAfterImages(images: ArrayList<AppImage>) {
        afterImageAdapter.changePath(images)
    }

    private fun checkValidate(): Boolean {
        binding.apply {
            if (!addServiceMode) return true
            if (etName.text.toString().isBlank()) {
                etName.displayErrorAndFocus(R.string.error_blank_name)
                return false
            }
            if (etPrice.text.toString().isBlank()) {
                etPrice.displayErrorAndFocus(R.string.error_blank_service_price)
                return false
            }
        }
        return true
    }

    private fun updateTotalValue() {
        var value = totalAmount
        var temp = discount
        if (listMoreService.isEmpty()) {
            totalAmount = value
            discount = temp
            return
        }
        if ((currentApmInfor.hasVoucher && !currentApmInfor.showPercent) || !currentApmInfor.hasVoucher) {
            listMoreService.forEach {
                value += it.price
            }
        } else {
            listMoreService.forEach {
                temp += it.price* (currentApmInfor.percent)/100
                value += it.price * (100 - currentApmInfor.percent) / 100
            }
        }
        totalAmount = value
        discount = temp
    }

    fun updateServiceSearch(item: Pair<List<IService>, Boolean>) {
        if (item.first.isEmpty() && !item.second) {
            binding.lvSearch.hide()
        }
        if (item.first.isNotEmpty()) {
            binding.lvSearch.show()
        }
        binding.loadingProgress.setLoading(item.second)
        searchServiceAdapter.submit(item.first)
    }

    private fun clearData() {
        binding.apply {
            tvFree.hide()
            etAmount.show()
            listMoreService.clear()
            moreServiceAdapter.clear()
            etName.clearText()
            etPrice.clearText()
            etNote.clearText()
            beforeImageAdapter.clear()
            afterImageAdapter.clear()
            addServiceMode = false
        }
    }

    fun show(
        apm: IAppointment,
        function: (Double, String, List<IService>) -> Unit
    ) {
        currentApmInfor = apm
        clearData()
        with(binding) {
            etName.bind {
                if (etName.isFocused) {
                    onSearchService.invoke(it)
                }
            }
            txtDiscount.show(apm.showPercent)
            txtDiscount.text = apm.percentDisplay
            txtPriceDiscount.text = apm.discountDisplay
            txtTotal.text = apm.totalPriceService
            etAmount.text = apm.totalAmountDisplay.toPriceValue()
            voucherLayout.show(apm.hasVoucher)
            val list = apm.serviceList.toMutableList()
            apm.serviceCustom.let { list.addAll(it) }
            (ServicePriceAdapter(rvServices)).apply {
                submit(list)
            }
            etNote.setText("")
            btSubmit.onClick {
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                function.invoke(amount, etNote.text.toString(), listMoreService)
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