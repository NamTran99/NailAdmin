package com.app.inails.booking.admin.model.ui.client

import com.app.inails.booking.admin.model.ui.ISalon
import com.app.inails.booking.admin.model.ui.ISalonDetail
import com.app.inails.booking.admin.model.ui.IService

interface IBooking : ISalonClient {
    val bookingID: Long get() = 0
    val bookingIdDisplay: String get() = ""
    val customerName: String get() = ""
    val customerPhone: String get() = ""
    val dateTime: String get() = ""
    val staffName: String get() = ""
    val note: String get() = ""
    val services: List<IServiceClient>? get() = listOf()
    val hasVoucher: Boolean get() = false
    val discount: String get() = ""
    val totalPrice: String get() = ""
    val totalAmount: String get() = ""
    val percent: String get() = ""
    val showPercent : Boolean get() =false
    val voucherInfo : String get() = ""
    val voucherCode: String get() = ""
}

interface IBookingNotification : IBooking, ISalonDetailClient {
    val id: Long get() = 0
    val reason: String get() = ""
    val notes: String get() = ""
}