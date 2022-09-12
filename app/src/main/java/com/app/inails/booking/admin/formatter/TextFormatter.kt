package com.app.inails.booking.admin.formatter

import android.support.di.Inject
import android.support.di.ShareScope
import android.telephony.PhoneNumberUtils
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.response.StaffDTO


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

    fun fullName(staffDTO: StaffDTO) = "${staffDTO.first_name} ${staffDTO.last_name}"

    fun formatStatusStaffStatus(staffDTO: StaffDTO): String {
        return when (staffDTO.status) {
            DataConst.StaffStatus.STAFF_WORKING -> "Busy ${staffDTO.appointment_processing?.date_appointment_format}"
            else -> staffDTO.status_name?:""
        }
    }

    fun formatStatusStaffIcon(status: Int?): Int {
        return when (status) {
            DataConst.StaffStatus.STAFF_BREAK -> R.drawable.circle_yellow
            DataConst.StaffStatus.STAFF_AVAILABLE -> R.drawable.circle_blue
            DataConst.StaffStatus.STAFF_WORKING -> R.drawable.circle_red
            else -> R.drawable.circle_yellow
        }
    }

    fun formatStatusStaffColor(status: Int?): Int {
        return when (status) {
            DataConst.StaffStatus.STAFF_BREAK -> R.color.yellow
            DataConst.StaffStatus.STAFF_AVAILABLE -> R.color.lightBlue02
            DataConst.StaffStatus.STAFF_WORKING -> R.color.red
            else -> R.color.yellow
        }
    }

    fun formatStatusAppointmentIcon(status: Int?): Int {
        return when (status) {
//            StaffDTO.STAFF_WORKING -> R.drawable.ic_oval_green
            else -> R.drawable.circle_yellow
        }
    }

    fun formatStatusAppointmentColor(status: Int?): Int {
        return when (status) {
            DataConst.AppointmentStatus.APM_CANCEL -> R.color.gray09
            DataConst.AppointmentStatus.APM_IN_PROCESSING -> R.color.lightBlue02
            else -> R.color.yellow
        }
    }
}