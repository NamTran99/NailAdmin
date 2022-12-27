package com.app.inails.booking.admin.model.ui.client

interface IFeedback {
    val total: String get() = ""
    val averageRating: Float get() = 0f
    val feedbacks: List<IFeedbackItem>? get() = listOf()
}

interface IFeedbackItem {
    val name: String get() = ""
    val phone: String get() = ""
    val rating: Float get() = 0f
    val time: String get() = ""
    val contents: String get() = ""
    val images: List<String>? get() = listOf()
}