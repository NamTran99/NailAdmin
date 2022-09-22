package com.app.inails.booking.admin.model.firebase

class FireBaseCloudMessage(
    val body: String,
    val customer_id: Any,
    val data_id: Int,
    val is_read: Int,
    val meta_data: MetaData,
    val data: Data,
    val salon_id: Int,
    val title: String,
    val type: Int
){
    data class MetaData(
        val sender: Sender
    )

    data class Sender(
        val customer_id: Int
    )

    data class Data(
        val appointment_id: Int
    )
}

