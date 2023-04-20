package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LayoutAddSalonServiceBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.SalonService
import com.app.inails.booking.admin.model.ui.IService
import com.bumptech.glide.Glide.init

interface IAddService{
    var onAddService: (SalonService) -> Unit
}

class AddSalonService(context: Context, attributeSet: AttributeSet? = null) : FrameLayout
    (context, attributeSet), IAddService {

    constructor(context: Context) : this(context, null)

    private val binding = viewBinding(LayoutAddSalonServiceBinding::inflate)

    private val mService : SalonService
        get() = SalonService(name = mServiceName, price = mServicePrice.toDouble())
    private val mServiceName: String
        get() = binding.etName.text.toString()
    private val mServicePrice:String
        get() = binding.etPrice.text.toString()
    init {
        setUpView()
    }

    private fun setUpView(){
        binding.apply {
            etName.bind {

            }
            btAdd.onClick{
                if(checkValidate()){
                    onAddService.invoke(mService)
                }
            }
        }
    }

    fun checkValidate():Boolean{
        if (mServiceName.isEmpty()) {
            binding.etName.displayErrorAndFocus(R.string.error_blank_service_name)
            return false
        }
        if(mServicePrice.isEmpty())
        {
            binding.etPrice.displayErrorAndFocus(R.string.error_blank_service_price)
            return false
        }
        if(mServicePrice.toDouble() <= 0.0){
            binding.etPrice.displayErrorAndFocus(R.string.error_price_not_greater_than_0)
        }
        return true
    }

    override var onAddService: (SalonService) -> Unit = {}
}
