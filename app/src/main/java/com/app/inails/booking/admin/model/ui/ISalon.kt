package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.MainApplication
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.DateTimeFormat
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.extention.toTime
import com.app.inails.booking.admin.factory.SalonFactory.Companion.VoucherHelper.getStatusColor
import com.app.inails.booking.admin.factory.SalonFactory.Companion.VoucherHelper.getStatusName
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

interface ISalon {
    val salonID: Long get() = 0
    val salonName: String get() = ""
    val address: String get() = ""
    val phoneNumber: String get() = ""
    val slug: String get() = ""
    val rating: Float get() = 0f
}

interface ISalonDTOOwner {
    val value: SalonDTO? get() = null
}

interface ISalonDetail : ISalon {
    val ownerName: String get() = ""
    val ownerPhone: String get() = ""
    val ownerEmail: String get() = ""
    val des: String? get() = ""
    val images: List<AppImage> get() = listOf()
    val lat: Float get() = 0f
    val email: String get() = ""
    val state: String get() = ""
    val city: String get() = ""
    val country: String get() = ""
    val zipCode: String get() = ""
    val lng: Float get() = 0f
    val schedules: List<ISchedule>? get() = listOf()
    val zoneID: String get() = ""
    val zoneOffSet: String get() = ""
    val tzDisplay1: String get() = "" // display at Update Salon Information (include Business Hour)
    val tzDisplay2: String get() = "" // display at Business Hour (not include Business Hour)
    val vouchers: List<IVoucher> get() = listOf()
    val afterGalleryImage: List<AppImage> get() = listOf()
    val beforeGalleryImage: List<AppImage> get() = listOf()
}

interface IVoucher {
    val id: Int get() = 0
    val code: String get() = ""
    val typeName: VoucherType get() = VoucherType.PERCENT // 1 : %  || 2 : value
    val startDate: String get() = ""
    val endDate: String get() = ""
    val typeCustomer: Int get() = R.string.customer_type_all // 1 : all || 2 : normal || 3 : vip
    val valueDiscount: String get() = ""
    val description: String get() = ""
    val isAlreadyFormatDate: Boolean get() = false
    val statusName: Int get() = 0
    val statusColor: Int get() = 0
    val status: Int get() = 0
}

enum class VoucherType {
    PERCENT, VALUE
}

class ISchedule(
    var day: Int = 0,
    @StringRes var dayFormat: Int = 0,  // day name
    var startTimeFormat: String? = null,// format 12H with AM/PM
    var endTimeFormat: String? = null,  // format 12H with AM/PM
    var startTime: String? = null,      // format 24H
    var endTime: String? = null,        // format 24H
    var timeFormat: String? = null,
    override var isSelector: Boolean = false,
    var isOpenDay : Boolean = false
) : Serializable, ISelector {
    override fun toString(): String {
        return "{\"day\":$day,\"start_time\":\"$startTime\",\"end_time\":\"$endTime\"}".replace(
            "\"null\"",
            "null"
        )
    }

    companion object {
        fun getDefaultList() = listOf(
            ISchedule(1, dayFormat = R.string.monday),
            ISchedule(2, dayFormat = R.string.tuesday),
            ISchedule(3, dayFormat = R.string.wednesday),
            ISchedule(4, dayFormat = R.string.thursday),
            ISchedule(5, dayFormat = R.string.friday),
            ISchedule(6, dayFormat = R.string.saturday),
            ISchedule(0, dayFormat = R.string.sunday),
        )
    }
}

@Parcelize
class VoucherForm(
    override var code: String = "",
    override var id: Int = -1,
    @SerializedName("start_date")
    override var startDate: String = "",
    @SerializedName("expiration_date")
    override var endDate: String = "",
    @SerializedName("value")
    override var valueDiscount: String = "",
    var salon_id : Int = 0,
    var type_customer: Int = 0, //1 : all || 2 : normal || 3 : vip
    override var description: String = "",
    override var typeName: VoucherType = VoucherType.PERCENT,
    override var typeCustomer: Int = R.string.customer_type_all,
    override var statusName: Int = 0,
    override var statusColor: Int = 0,
    override val status: Int = 4,
    var type: Int = 1, //1  : %  || 2 : value
    var title: String = "a",
    var listExistCode: List<String> = listOf(),
    override var isAlreadyFormatDate: Boolean = true
): IVoucher, Parcelable{
    fun convertToVoucher(){
        typeName =   when (type) {
            1 -> VoucherType.PERCENT
            else -> VoucherType.VALUE
        }
        typeCustomer= when (type_customer) {
            1 -> R.string.customer_type_all
            2 -> R.string.customer_type_normal
            else -> R.string.customer_type_vip
        }
        statusName = getStatusName(4)
        statusColor = getStatusColor(4)
    }

    fun validate(){
        if(code.isBlank()){
            viewError(R.id.etCode, R.string.error_code_blank)
        }
        if(code.trim().length < 3){
            viewError(R.id.etCode, R.string.error_code_not_enough)
        }
        if(code in listExistCode){
            viewError(R.id.etCode, R.string.error_code_already_exist)
        }
        if(valueDiscount.isBlank()){
            viewError(R.id.etValue, R.string.error_empty_value)
        }
        if(valueDiscount.toFloat() <=0 ){
            viewError(R.id.etValue, R.string.error_value_discount_greater_than_0)
        }
        if(startDate.isBlank()){
            resourceError(R.string.error_blank_start_time_1)
        }
        if(endDate.isBlank()){
            resourceError(R.string.error_blank_end_time_1)
        }
        if(startDate.toTime(DateTimeFormat.format3) >= endDate.toTime(DateTimeFormat.format3)){
            resourceError(R.string.message_end_time_greather_than_start_time_2)
        }
        if(type_customer == 0){
            resourceError(R.string.error_not_select_customer_type)
        }
    }
}

class SalonForm(
    var allImageCount: Int = 0,
    var fullTimeZoneDisplay1: String = "", // display at Update Salon Information (include Business Hour)
    var fullTimeZoneDisplay2: String = "", // display at Business Hour (not include Business Hour)
    var email: String = "",
    var id: Int = 0,
    var name: String = "",
    var phone: String = "",
    var address: String = "",
    var state: String = "",
    var city: String = "",
    var zipCode: String = "",
    var country: String = "",
    var description: String = "",
    var zoneID: String = "",
    var offsetDisplay: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var images: List<AppImage> = listOf(),
    val deleteImage: MutableList<Int> = mutableListOf(),
    var schedules: List<ScheduleForm> = listOf()
) {
    fun setUpDataTimeZone(offset: String, zoneID: String) {
        offsetDisplay = "UTC $offset"
        this.zoneID = zoneID
        fullTimeZoneDisplay1 = MainApplication.applicationContext().getString(R.string.business_hour_format, zoneID, offsetDisplay)
        fullTimeZoneDisplay2 = "${zoneID} ${offsetDisplay}"
    }

    fun validate() {
        if (name.isBlank()) {
            viewError(
                R.id.etSalonName,
                R.string.error_blank_salon_name
            )
        }
        if(phone.trim().isEmpty()){
            viewError(R.id.etPhone, R.string.error_blank_phone)
        }
        if(phone.trim().convertPhoneToNormalFormat().length < 10){
            viewError(R.id.etPhone, R.string.error_type_phone_not_enough)
        }
        if (allImageCount == 0) {
            resourceError(R.string.error_not_enough_image_count)
        }
    }
}

@Parcelize
data class ScheduleForm(
    val day: Int = 0,
    @SerializedName("start_time")
    val startTime: String? = null, // Format HH:MM:SS
    @SerializedName("end_time")
    val endTime: String? = null,    // Format HH:MM:SS
) : Parcelable {
    override fun toString(): String {
        return "{\"day\":$day,\"start_time\":\"$startTime\",\"end_time\":\"$endTime\"}".replace(
            "\"null\"",
            "null"
        )
    }
}