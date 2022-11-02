package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface ICustomer {
    val id: Int get() = 0
    val name: String get() = ""
    val phone: String get() = ""
    val email: String get() = ""
    val address: String get() = ""
    val birthDay: String get()  = ""
}
@Parcelize
class CustomerImpl: ICustomer,Parcelable