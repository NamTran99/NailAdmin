package com.app.inails.booking.admin.model.ui

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.extention.isEmail
import com.app.inails.booking.admin.utils.TimeUtils


data class SignUpForm constructor(
    var zoneID: String = TimeUtils.getZoneID(), // not use for request
    var offsetDisplay: String = TimeUtils.getTimeZoneOffSet(), // not use for request
    var phone: String = "",
    var password: String = "",
    var salon_name: String = "",
    var salon_address: String = "",
    var salon_phone: String = "",
    var salon_city: String = "",
    var salon_tz: String = "",
    var salon_timezone: String = "UTC",
    var salon_lat: Double = 0.0,
    var salon_lng: Double = 0.0,
    var salon_zipcode: String = "",
    var salon_country: String = "",
    var admin_name: String = "",
    var salon_email: String = "",
    var salon_state: String = "",
    var salon_description: String = "",
    var schedules: MutableList<ISchedule> = ISchedule.getDefaultList().toMutableList(),
    var images: MutableList<AppImage> = mutableListOf(),
    val staffs: MutableList<ISalonStaff> = mutableListOf(),
    val services: MutableList<ISalonService> = mutableListOf(),
){
    fun getTimeZoneDisplay(showFull: Boolean): String{
        return if(showFull){
             "Business Hour (${zoneID} ${offsetDisplay})"
        }else "${zoneID} ${offsetDisplay}"
    }

    fun validate(){
        if(phone.trim().isEmpty()){
            viewError(R.id.edtAccPhone, R.string.error_blank_phone)
        }
        if(phone.trim().convertPhoneToNormalFormat().length < 10){
            viewError(R.id.edtAccPhone, R.string.error_type_phone_not_enough)
        }
        if(password.trim().isEmpty()){
            viewError(R.id.edtAccPassword, R.string.error_blank_password_2)
        }
        if(admin_name.isBlank()){
            viewError(R.id.edtAccName, R.string.error_blank_admin_name)
        }
        if(salon_name.trim().isEmpty()){
            viewError(R.id.edtSalonName, R.string.error_blank_salon_name)
        }
        if(salon_address.trim().isEmpty()){
            viewError(R.id.edtSalonAddress, R.string.error_blank_salon_address)
        }
        if(salon_phone.trim().isEmpty()){
            viewError(R.id.edtSalonPhone, R.string.error_blank_salon_phone)
        }
        if(salon_phone.trim().convertPhoneToNormalFormat().length < 10){
            viewError(R.id.edtSalonPhone, R.string.error_type_phone_not_enough)
        }
        if(salon_email.isBlank()){
            viewError(R.id.edtSalonEmail, R.string.error_blank_admin_email)
        }

        if (!salon_email.isEmail()) viewError(
            R.id.edtSalonEmail,
            R.string.error_not_correct_email
        )

        if(images.size == 0){
            resourceError(R.string.error_not_enough_image_count)
        }
        if(salon_description.trim().isEmpty()){
            viewError(R.id.edtDescription, R.string.error_blank_salon_description)
        }
        if(services.size == 0){
            resourceError(R.string.error_not_enough_service)
        }
        if(staffs.size == 0){
            resourceError(R.string.error_not_enough_staff)
        }
    }
}

data class ISalonStaff(
    val first_name: String = "",
    val last_name: String = "",
    val phone: String = "",
    @Transient
    val fullName: String = ""
)

data class ISalonService(
    val name: String = "",
    val price: Double = 0.0
)