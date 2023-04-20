package com.app.inails.booking.admin.model.form

import android.webkit.WebResourceError
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.ResourceException
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.extention.isEmail

class UpdateCustomerForm(
    var id: Int = 0,
    var type: Int = 2,
    var note: String = "",
    var birthday: String = "",
    var email: String = "",
    var address: String = "",
    var name: String = "",
    var phone: String = ""
) {
    fun validate() {
        if(name.isBlank()){
            resourceError(R.string.error_blank_name)
        }
        if(phone.trim().isEmpty()){
            resourceError(R.string.error_blank_phone)
        }
        if(phone.trim().convertPhoneToNormalFormat().length < 10){
            resourceError(R.string.error_type_phone_not_enough)
        }
        if (!email.isEmail() && email.isNotBlank()) {
            resourceError(
                R.string.error_not_correct_email
            )
        }
    }
}