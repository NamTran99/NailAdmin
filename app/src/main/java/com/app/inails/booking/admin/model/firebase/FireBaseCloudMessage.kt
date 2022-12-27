package com.app.inails.booking.admin.model.firebase

data class FireBaseCloudMessage(
    val body: String,
    val `data`: Data? = Data(),
    val id: Int,
    val title: String,
    val type: String
)