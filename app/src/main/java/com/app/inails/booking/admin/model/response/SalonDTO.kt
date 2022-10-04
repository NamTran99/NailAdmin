package com.app.inails.booking.admin.model.response

import com.google.gson.annotations.SerializedName

data class SalonDTO(
    val id: Long = 0,
    val lat: Float = 0f,
    val lng: Float = 0f,
    val name: String? = "",
    var email: String? = "",
    val slug: String? = "",
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
    val schedules: List<Schedule>? = null
)

data class Image(
    val id: Long = 0,
    val image: String = ""
)

data class Schedule(
    val id: Long = 0,
    @SerializedName("day_name")
    val dayName: String = "",
    @SerializedName("start_time_format")
    val startTime: String? = "",
    @SerializedName("end_time_format")
    val endTime: String?= ""
)

data class Partner(
    val id: Long = 0,
    val name: String = ""
)