package com.app.inails.booking.admin.model.ui.client


interface IVoucherClient {
    val id: Long get() = 0
    val title: String get() = ""
    val code: String get() = ""
    val discount: String get() = ""
    val type: Int get() = 1
    val percent: String get() = ""
    val totalAmount : String get()=""
    val description : String get()=""
    val startDate : String get()=""
    val expirationDate : String get()=""
}
