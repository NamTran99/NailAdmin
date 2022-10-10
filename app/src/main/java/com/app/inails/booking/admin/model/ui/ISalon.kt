package com.app.inails.booking.admin.model.ui

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
    val lng: Float get() = 0f
    val schedules: List<ISchedule>? get() = listOf()
}

class ISchedule(
    var day: Int = 0,
    val dayFormat: String = "",
    var startTimeFormat: String? = null,
    var endTimeFormat: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var timeFormat: String? = null,
): Serializable

class SalonForm(
    var id: Int = 0,
    var name: String = "",
    var phone: String = "",
    var address: String = "",
    var description: String = "",
    var images: List<SalonImage> = listOf(),
    val deleteImage: MutableList<Int> = mutableListOf(),
    var schedules: List<ScheduleForm> = listOf()
)

class ScheduleForm(
    val day: Int = 0,
    @SerializedName("start_time")
    val startTime: String? = null,
    @SerializedName("end_time")
    val endTime: String? = null,
)

data class SalonImage(
    val id: Int? = null,
    val path: String = ""
)