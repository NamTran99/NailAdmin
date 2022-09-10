package com.app.inails.booking.formatter

import android.support.di.Inject
import android.support.di.ShareScope
import android.telephony.PhoneNumberUtils


@Inject(ShareScope.Singleton)
class TextFormatter {
    fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "").trim()
    }


    fun formatPhoneUS(phone: String?): String {
        return if (phone.isNullOrEmpty()) ""
        else PhoneNumberUtils.formatNumber(phone, "US")
    }
//
//    fun formatStatusForStaff(status: Int?): Int {
//        return when (status) {
//            StaffDTO.STAFF_WORKING -> R.string.text_status_available
//            else -> R.string.text_status_busy
//        }
//    }
//
//    fun formatStatusIcon(status: Int?): Int {
//        return when (status) {
//            StaffDTO.STAFF_WORKING -> R.drawable.ic_oval_green
//            else -> R.drawable.ic_oval_yellow
//        }
//    }
}