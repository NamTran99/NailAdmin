package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import android.support.core.route.BundleArgument
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.Schedule
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
    val images: List<SalonImage> get() = listOf()
    val lat: Float get() = 0f
    val email: String get() = ""
    val state: String get() = ""
    val city: String get() = ""
    val country: String get() = ""
    val zipCode: String get() = ""
    val lng: Float get() = 0f
    val schedules: List<ISchedule>? get() = listOf()
    val tzDisplay: String get() = ""
}

class ISchedule(
    var day: Int = 0,
    var dayFormat: String = "",
    var startTimeFormat: String? = null,// format 12H with AM/PM
    var endTimeFormat: String? = null,  // format 12H with AM/PM
    var startTime: String? = null,      // format 24H
    var endTime: String? = null,        // format 24H
    var timeFormat: String? = null,
): Serializable

class SalonForm(
    var timeZone : String = "",
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
    var images: List<SalonImage> = listOf(),
    val deleteImage: MutableList<Int> = mutableListOf(),
    var schedules: List<ScheduleForm> = listOf()
)

@Parcelize
data class ScheduleForm(
    val day: Int = 0,
    @SerializedName("start_time")
    val startTime: String? = null, // Format HH:MM:SS
    @SerializedName("end_time")
    val endTime: String? = null,    // Format HH:MM:SS
):  Parcelable{
    override fun toString(): String {
        return "{\"day\":\"$day\",\"start_time\":\"$startTime\",\"end_time\":\"$endTime\"}".replace("\"null\"", "null")
    }
}

data class SalonImage(
    val id: Int? = null,
    val path: String = ""
)