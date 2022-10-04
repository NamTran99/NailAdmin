package com.app.inails.booking.admin.model.ui

import com.app.inails.booking.admin.model.response.SalonDTO

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
    val images: List<String>? get() = listOf()
    val lat: Float get() = 0f
    val lng: Float get() = 0f
    val schedules: List<Pair<String, String>>? get() = listOf()
}