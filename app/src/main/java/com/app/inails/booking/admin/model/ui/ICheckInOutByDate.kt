package com.app.inails.booking.admin.model.ui

interface ICheckInOutByDate{
    val dateFormat: String get() = ""
    val details: List<ICheckInOutDetail> get() = listOf()
}
