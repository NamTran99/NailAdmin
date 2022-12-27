package com.app.inails.booking.admin.model.response

data class FeedbackDTO(
    val id: Long? = 0,
    val content: String,
    val rating : Float,
    val images: List<FeedBackImageDTO>
)

data class FeedBackImageDTO(
    val image : String
)