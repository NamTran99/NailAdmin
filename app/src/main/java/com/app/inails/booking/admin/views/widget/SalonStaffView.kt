package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LayoutSalonStaffBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.SalonService
import com.app.inails.booking.admin.model.ui.SalonStaff


class SalonStaffView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout
    (context, attributeSet) {

    constructor(context: Context) : this(context, null)

    companion object {
        val listService = listOf<SalonService>()
    }

    private val binding = viewBinding(LayoutSalonStaffBinding::inflate)

    var onCLickImage : ((String) -> Unit) = {}
    var onCLickRemoveImage : (() -> Unit) = {}

    var displayType: DisplayType = DisplayType.TypeService
        set(value) {
            binding.etPhone.isFocusable = value == DisplayType.TypeService
            binding.lvTypeStaff.show(value == DisplayType.TypeService)
            binding.lvDisplayStaff.show(value == DisplayType.DisplayService)
            if (value == DisplayType.TypeService) {
                binding.btnDelete.hide()
            } else {
                binding.imgDisplayAvatar.hideClearImage()
                binding.btnDelete.show()
            }
            field = value
        }

    var price: String = ""
        get() = binding.etPhone.text.toString()
        set(value) {
            binding.etPhone.setText(value)
            field = value
        }

    var url: String = ""
        set(value){
            binding.apply {
                imgDisplayAvatar.setImageUrl(value)
                imgEditAvatar.setImageUrl(value)
            }
            field = value
        }

    var staffData: SalonStaff = SalonStaff()
        get() =
            SalonStaff(
                imageUri= url,
                name = binding.etFullName.text.toString(),
                phone =  binding.etPhone.text.toString().convertPhoneToNormalFormat()
            )
        set(value){
            url = value.imageUri
            binding.tvStaffFullName.text  = value.name
            if(displayType == DisplayType.DisplayService){
                binding.tvPhone.text = value.phone.formatPhoneUSCustom()
            }else{
                binding.etPhone.setText(value.phone)
            }
            field = value
        }

    var fullName: String = ""
        set(value) {
            binding.tvStaffFullName.text = value
            field = value
        }

    var onClickDelete: ((View) -> Unit)? = null

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
        binding.etPhone.requestFocus()
    }

    fun resetView() {
        binding.apply {
            staffData = SalonStaff()
            imgEditAvatar.removePhoto()
            etFullName.setText("")
            etPhone.setText("")
            tag = null
        }
    }

    fun updateAvatar(image: String?) {
        image?.let{
            url = it
        }
    }
    
    fun checkValidate():Boolean{
        binding.apply {

            if(etFullName.text.isBlank()){
                binding.etFullName.error = context.getString(R.string.error_full_name_blank)
                binding.etFullName.setText("")
                binding.etFullName.requestFocus()
                return false
            }

            if(etPhone.text.trim().isEmpty()){
                binding.etPhone.error = context.getString(R.string.error_blank_phone)
                binding.etPhone.setText("")
                binding.etPhone.requestFocus()
                return false
            }

            if(etPhone.text.trim().toString().convertPhoneToNormalFormat().length < 10){
                binding.etPhone.error =context.getString(R.string.error_type_phone_not_enough)
                binding.etPhone.requestFocus()
                return false
            }
            return  true
        }
    }

    private fun setUpView() {
        binding.apply {
            etPhone.inputTypePhoneUS()
            lvAddImage.onClick{
                onCLickImage.invoke(url)
            }
            imgDisplayAvatar.onClick{
                onCLickImage.invoke(url)
            }
            imgDisplayAvatar.onClickClearImage = {
                onCLickRemoveImage.invoke()
            }
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