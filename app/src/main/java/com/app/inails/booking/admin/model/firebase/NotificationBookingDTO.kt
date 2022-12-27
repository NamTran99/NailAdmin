package com.app.inails.booking.admin.model.firebase

import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.response.client.ServiceClientDTO
import com.google.gson.annotations.SerializedName

data class NotificationBookingDTO(
    @SerializedName("date_appointment_timestamp")
    val datetime: Long?,
    val id: Long?,
    @SerializedName("salon_slug")
    val salonSlug: String?,
    @SerializedName("salon_name")
    val salonName: String?,
    @SerializedName("salon_id")
    val salonId: Long?,
    @SerializedName("salon_lat")
    val lat:Float?,
    @SerializedName("salon_lng")
    val lng:Float?,
    val service_custom: Any?,
    val service_custom_name: Any?,
    val services: List<ServiceClientDTO?>?,
    @SerializedName("staff_name")
    val staffName: String?,
    @SerializedName("reason_cancel")
    val reasonCancel: String?,
    val price: Float?,
    val notes: String?,
    @SerializedName("customer_name")
    val customerName:String="",
    @SerializedName("customer_phone")
    val customerPhone:String=""
)