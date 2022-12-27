package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LayoutSalonStaffBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.ISalonService
import com.app.inails.booking.admin.model.ui.ISalonStaff


class SalonStaffView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout
    (context, attributeSet) {

    constructor(context: Context) : this(context, null)

    companion object {
        val listService = listOf<ISalonService>()
    }

    private val binding = viewBinding(LayoutSalonStaffBinding::inflate)

    var displayType: DisplayType = DisplayType.TypeService
        set(value) {
            binding.tvStaffFullName.show(value == DisplayType.ShowOneService)
            binding.lvEdtName.show(value == DisplayType.TypeService)
            binding.etPhone.isFocusable = value == DisplayType.TypeService
            if (value == DisplayType.TypeService) {
                binding.btnDelete.hide()
            } else {
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

    var staffData: ISalonStaff = ISalonStaff()
        get() =
            ISalonStaff(
                first_name = binding.etStaffFirstName.text.toString(),
                last_name = binding.etStaffLastName.text.toString(),
                phone =  binding.etPhone.text.toString().convertPhoneToNormalFormat()
            )
        set(value){
            binding.tvStaffFullName.text  = "${value.last_name} ${value.first_name}"
            binding.etPhone.setText(value.phone)
            field = value
        }


    var fullName: String = ""
        set(value) {
            binding.tvStaffFullName.text = value
            field = value
        }

    var lastName:String = ""
        get() = binding.etStaffLastName.text.toString()

    var firstName:String = ""
        get() = binding.etStaffFirstName.text.toString()

    var onClickDelete: ((View) -> Unit)? = null
    val etPrice = binding.etPhone

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
            etStaffLastName.setText("")
            etStaffFirstName.setText("")
            etPhone.setText("")
            tag = null
        }
    }
    
    fun checkValidate():Boolean{
        binding.apply {
            if(etStaffLastName.text.trim().isEmpty()){
                binding.etStaffLastName.error = "You must type last name"
                binding.etStaffLastName.setText("")
                binding.etStaffLastName.requestFocus()
                return false
            }

            if(etStaffFirstName.text.trim().isEmpty()){
                binding.etStaffFirstName.error = "You must type first name"
                binding.etStaffFirstName.setText("")
                binding.etStaffFirstName.requestFocus()
                return false
            }

            if(etPhone.text.trim().isEmpty()){
                binding.etPhone.error = "You must type phone number"
                binding.etPhone.setText("")
                binding.etPhone.requestFocus()
                return false
            }

            if(etPhone.text.trim().toString().convertPhoneToNormalFormat().length < 10){
                binding.etPhone.error = "Phone must be 10 characters long"
                binding.etPhone.requestFocus()
                return false
            }
            return  true
        }
    }

    private fun setUpView() {
        binding.apply {
            etPhone.inputTypePhoneUS()
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