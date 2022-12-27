package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.model.response.SalonDTO
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
    val code: String get() = ""
    val type: VoucherType get() = VoucherType.PERCENT // 1 : %  || 2 : value
    val startDate: String get() = ""
    val endDate: String get() = ""
    val typeCustomer: String get() = "All" // 1 : all || 2 : normal || 3 : vip
    val value: Double get() = 0.0 // 1 : all || 2 : normal || 3 : vip
    val description: String get() = ""
}

enum class VoucherType {
    PERCENT, VALUE
}

class ISchedule(
    var day: Int = 0,
    var dayFormat: String = "",
    var startTimeFormat: String? = null,// format 12H with AM/PM
    var endTimeFormat: String? = null,  // format 12H with AM/PM
    var startTime: String? = null,      // format 24H
    var endTime: String? = null,        // format 24H
    var timeFormat: String? = null,
) : Serializable {
    override fun toString(): String {
        return "{\"day\":$day,\"start_time\":\"$startTime\",\"end_time\":\"$endTime\"}".replace(
            "\"null\"",
            "null"
        )
    }

    companion object {
        fun getDefaultList() = listOf(
            ISchedule(1, timeFormat = "Not open", dayFormat = "Monday"),
            ISchedule(2, timeFormat = "Not open", dayFormat = "Tuesday"),
            ISchedule(3, timeFormat = "Not open", dayFormat = "Wednesday"),
            ISchedule(4, timeFormat = "Not open", dayFormat = "Thursday"),
            ISchedule(5, timeFormat = "Not open", dayFormat = "Friday"),
            ISchedule(6, timeFormat = "Not open", dayFormat = "Saturday"),
            ISchedule(0, timeFormat = "Not open", dayFormat = "Sunday"),
        )
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
        fullTimeZoneDisplay1 = "Business Hour (${zoneID} ${offsetDisplay})"
        fullTimeZoneDisplay2 = "${zoneID} ${offsetDisplay}"
    }

    fun validate() {
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

@Parcelize
data class AppImage(
    val id: Int? = null,
    val path: String = ""
) : Parcelable