package com.app.inails.booking.admin.model.response.client

import com.app.inails.booking.admin.model.response.*
import com.google.gson.annotations.SerializedName

data class BookingClientDTO(
    val canceled_at: Any?,
    val canceled_by: Any?,
    val created_at: String?,
    val customer: UserDTO.Data?,
    val customer_id: Int?,
    @SerializedName("customer_name")
    val customerName: String?,
    val date_appointment: String?,
    @SerializedName("date_appointment_format")
    val datetimeDisplay: String?,
    @SerializedName("date_appointment_timestamp")
    val dateTimestamp:Long?,
    val date_finished: Any?,
    val deleted_at: Any?,
    val id: Long?,
    @SerializedName("list_service_names")
    val serviceForAppointment: String?,
    val list_service_names_with_price: String?,
    val notes: String?,
    val price: Float?,
    val rating: Any?,
    val reason_cancel: String?,
    val reviews: Any?,
    val salon: SalonClientDTO?,
    @SerializedName("salon_id")
    val salonId: Long?,
    val service_custom: Any?,
    val service_custom_name: Any?,
    val services: List<ServiceClientDTO?>?,
    val staff: StaffDTO?,
    val staff_id: Int?,
    @SerializedName("staff_name")
    val staffName: String?,
    val status: Int?,
    val status_class: String?,
    val status_name: String?,
    val total_price_service: Float?,
    val type: Int?,
    val updated_at: Any?,
    val work_time: Any?,
    @SerializedName("canceled_by_name")
    val canceledByName: String?,
    val feedback : FeedbackItemDTO?,
    @SerializedName("created_at_timestamp")
    val createAtTimestamp:Long?,
    @SerializedName("note_appointment")
    val note:String?,
    val images: List<Image>?,
    val voucher: VoucherClientDTO?,
    @SerializedName("total_discount")
    val totalDiscount: Float?,
    @SerializedName("total_price")
    val totalPrice: Float?,
)