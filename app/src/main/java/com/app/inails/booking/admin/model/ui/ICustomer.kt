package com.app.inails.booking.admin.model.ui

interface ICustomer {
    val id: Int get() = 0
    val name: String get() = ""
    val phone: String get() = ""
    val email: String get() = ""
    val address: String get() = ""
}

class CustomerImpl: ICustomer