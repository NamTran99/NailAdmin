package com.app.inails.booking.admin.model.response

 
import com.google.gson.annotations.SerializedName

data class SalonDTO(
    val id: Long = 0,
    val lat: Float = 0f,
    val lng: Float = 0f,
    var name: String? = "",
    var email: String = "",
    var country: String = "",
    var slug: String? = "",
    val address: String? = "",
    val city: String? = "",
    val state: String? = "",
    val zipcode: String? = "",
    val phone: String? = "",
    val rating: Float? = 0f,
    val description: String? = "",
    @SerializedName("full_address")
    val fullAddress: String? = "",
    val partner: Partner? = null,
    val images: List<Image>? = null,
    val schedules: List<Schedule>? = null,
    val timezone: String? = "",
    val tz: String? = "",
    val vouchers: List<VoucherDTO> = listOf(),
    val gallery: List<GalleryImage> = listOf(),
    val created_at: String? = null,
    val owner_name: String? = "",
    val workplace: String? = ""
)

data class GalleryImage(
    val id: Int? = 0,
    val image: String? = "",
    val type: Int? = 1, //1: before, 2 after,
    @SerializedName("type_name")
    val typeName : String = ""
)

class VoucherDTO(
    val id: Int = 0,
    val code: String? = "",
    val type: Int? = 1, // 1 : %  || 2 : value
    val start_date: String? = "",
    val expiration_date: String? = "",
    val type_customer: Int? = 1, // 1 : all || 2 : normal || 3 : vip
    val value: Double = 0.0, // 1 : all || 2 : normal || 3 : vip
    val description: String? = "",
    val isAlreadyFormatDate: Boolean = false,
    val status: Int = 1
    )

 class Image(
    val id: Long = 0,
    val image: String = ""
)

data class Schedule(
    val id: Long = 0,
    @SerializedName("day_name")
    val dayName: String = "",
    @SerializedName("start_time_format")
    val startTimeFormat: String? = "",
    @SerializedName("end_time_format")
    val endTimeFormat: String? = "",
    @SerializedName("start_time")
    val startTime: String? = "",
    @SerializedName("end_time")
    val endTime: String? = "",
    val day: Int = 0
)

data class Partner(
    val id: Long = 0,
    val name: String? = "",
    val phone: String ? = "",
    val email: String ? = ""
)