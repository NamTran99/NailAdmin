package com.app.inails.booking.admin.model.firebase

data class FireBaseCloudMessage(
    val body: String,
    val `data`: Data,
    val id: String,
    val title: String,
    val type: String
)