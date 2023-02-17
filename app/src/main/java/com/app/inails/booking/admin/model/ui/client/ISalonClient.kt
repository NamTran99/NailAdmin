package com.app.inails.booking.admin.model.ui.client

import com.app.inails.booking.admin.model.response.SalonDTO
 
import com.app.inails.booking.admin.model.ui.ISalon

interface ISalonClient {
    val salonID: Long get() = 0
    val salonName: String get() = ""
    val address: String get() = ""
    val phoneNumber: String get() = ""
    val slug: String get() = ""
    val rating: Float get() = 0f
    val email: String get() = ""
}

interface ISalonDTOOwner {
    val value: SalonDTO? get() = null
}

interface ISalonDetailClient : ISalonClient {
    val ownerName: String get() = ""
    val des: String? get() = ""
    val images: List<String>? get() = listOf()
    val lat: Float get() = 0f
    val lng: Float get() = 0f
    val schedules: Pair<List<Pair<String, String>>?, String> get() = Pair(listOf(), "")
    val imagesBefore: List<String>? get() = listOf()
    val imagesAfter: List<String>? get() = listOf()
}