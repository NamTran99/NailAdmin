package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LayoutManicuristServiceBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.SalonService


enum class DisplayType(val index: Int) {
    TypeService(0),
    DisplayService(1);

    companion object {
        fun getTypeByIndex(index: Int): DisplayType {
            DisplayType.values().forEach {
                if (it.index == index) return it
            }
            return DisplayType.TypeService
        }
    }
}

class SalonServiceView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout
    (context, attributeSet) {

    constructor(context: Context) : this(context, null)

    companion object {
        val listService = listOf<SalonService>()
    }

    private val binding = viewBinding(LayoutManicuristServiceBinding::inflate)

    var onCLickImage: ((String) -> Unit) = {}
    var onCLickRemoveImage: (() -> Unit) = {}

    var displayType: DisplayType = DisplayType.TypeService
        set(value) {
            binding.etPrice.isFocusable = value == DisplayType.TypeService
            if (value == DisplayType.TypeService) {
                binding.lvTypeService.show()
                binding.lvDisplay.hide()
                binding.btnDelete.hide()
            } else {
                binding.lvDisplay.show()
                binding.lvTypeService.hide()
                binding.imgDisplayAvatar.hideClearImage()
                binding.btnDelete.show()
            }
            field = value
        }

    var url: String = ""
        set(value) {
            binding.apply {
                imgEditImage.setImageUrl(value)
                imgDisplayAvatar.setImageUrl(value)
            }
            field = value
        }

    var price: String = ""
        get() = binding.etPrice.text.toString().trim()
        set(value) {
            binding.tvService.text = value
            binding.etPrice.setText(value)
            field = value
        }

    var serviceName: String = ""
        set(value) {
            binding.tvService.text = value
            field = value
        }
        get() =
            if (displayType == DisplayType.TypeService) {
                binding.etService.text.toString().trim()
            } else {
                binding.tvService.text.toString().trim()
            }

    var service: SalonService = SalonService()
        get() = SalonService(
            imageUri = url,
            name = binding.etService.text.toString(),
            price = binding.etPrice.text.toString().toDouble()
        )
        set(value) {
            url = value.imageUri
            binding.tvService.text = value.name
            binding.tvPrice.text = value.price.toString()
            field = value
        }

    var onClickDelete: ((View) -> Unit)? = null
    val etPrice = binding.etPrice

    init {
        context.loadAttrs(attributeSet, R.styleable.SalonServiceView) {
            displayType = DisplayType.getTypeByIndex(
                it.getInt(
                    R.styleable.SalonServiceView_custom_display_type,
                    0
                )
            )
        }

        setUpView()
        setUpListener()
    }

    fun requestFocusPrice() {
        binding.etPrice.requestFocus()
    }

    fun checkValidate(): Boolean {
        if (serviceName.isEmpty()) {
            binding.etService.error = context.getString(R.string.error_blank_service_name)
            binding.etService.setText("")
            binding.etService.requestFocus()
            return false
        }
        if (price.isEmpty()) {
            binding.etPrice.error = context.getString(R.string.error_blank_service_price)
            binding.etPrice.setText("")
            binding.etPrice.requestFocus()
            return false
        }
        if (price.toDouble() <= 0.0) {
            binding.etPrice.error = context.getString(R.string.error_price_not_greater_than_0)
            binding.etPrice.setText("")
            binding.etPrice.requestFocus()
            return false
        }
        return true
    }

    fun resetView() {
        binding.apply {
            service = SalonService()
            imgEditImage.removePhoto()
            etService.setText("")
            etPrice.setText("")
            tag = null
        }
    }

    private fun setUpView() {
        binding.apply {
            etPrice.filters = arrayOf(DecimalDigitsInputFilter(3, 2))
            lvAddImage.onClick {
                onCLickImage.invoke(url)
            }
            imgDisplayAvatar.onClick{
                onCLickImage.invoke(url)
            }
            imgEditImage.onClickClearImage = {
                onCLickRemoveImage.invoke()
            }
        }
    }

    fun updateAvatar(image: String?) {
        image?.let {
            url = it
        }
    }


    private fun setUpListener() {
        binding.apply {
            btnDelete.setOnClickListener {
                onClickDelete?.invoke(it)
            }
        }
    }
}