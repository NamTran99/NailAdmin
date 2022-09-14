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
            else -> staffDTO.status_name ?: ""
        }
    }

    fun formatStatusStaffIcon(status: Int?): Int {
        return when (status) {
            DataConst.StaffStatus.STAFF_BREAK -> R.drawable.circle_yellow
            DataConst.StaffStatus.STAFF_AVAILABLE -> R.drawable.circle_green
            DataConst.StaffStatus.STAFF_WORKING -> R.drawable.circle_red
            else -> R.drawable.circle_yellow
        }
    }

    fun formatStatusStaffColor(status: Int?): Int {
        return when (status) {
            DataConst.StaffStatus.STAFF_BREAK -> R.color.yellow
            DataConst.StaffStatus.STAFF_AVAILABLE -> R.color.green
            DataConst.StaffStatus.STAFF_WORKING -> R.color.red
            else -> R.color.yellow
        }
    }
    fun formatBackgroundStaffColor(active: Int): Int {
        return if (active == 1) R.color.white else R.color.gray11

    }

    fun formatStatusAppointmentIcon(status: Int?,type: Int): Int {
        return when (status) {
            DataConst.AppointmentStatus.APM_CANCEL -> R.drawable.circle_gray
            DataConst.AppointmentStatus.APM_ACCEPTED -> R.drawable.circle_blue
            DataConst.AppointmentStatus.APM_WAITING -> if (type == 1)R.drawable.circle_orange else R.drawable.circle_yellow
            else -> R.drawable.circle_yellow
        }
    }

    fun formatStatusAppointmentColor(status: Int?,type : Int): Int {
        return when (status) {
            DataConst.AppointmentStatus.APM_CANCEL -> R.color.gray09
            DataConst.AppointmentStatus.APM_ACCEPTED -> R.color.lightBlue02
            DataConst.AppointmentStatus.APM_WAITING -> if (type == 1) R.color.orange else  R.color.yellow
            else -> R.color.yellow
        }
    }

    fun formatStatusAppointmentCancel(cancelledBy: String?): String {
        if (!cancelledBy.isNullOrEmpty()) {
            return "Cancelled by $cancelledBy"
        }
        return ""
    }
}