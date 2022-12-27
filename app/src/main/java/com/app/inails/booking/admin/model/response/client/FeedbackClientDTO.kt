package com.app.inails.booking.admin.model.response.client

data class FeedbackClientDTO(
    val average_rating: Float?,
    val feedbacks: List<FeedbackItemDTO?>?,
    val total: Int?
)

data class FeedbackItemDTO(
    val booking_id: Int?,
    val content: String?,
    val created_at: String?,
    val created_at_timestamp: Long?,
    val customer_id: Int?,
    val customer_name: String?,
    val customer_phone: String?,
    val diff_time: String?,
    val email: String?,
    val id: Int?,
    val name: String?,
    val rating: Float?,
    val salon_id: Int?,
    val updated_at: Any?,
    val images: List<Image>? = null,
)