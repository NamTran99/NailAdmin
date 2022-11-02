package com.app.inails.booking.admin.formatter

import android.support.di.Inject
import android.support.di.ShareScope
import android.telephony.PhoneNumberUtils
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.formatDateAppointment
import com.app.inails.booking.admin.extention.toCreatedAt
import com.app.inails.booking.admin.extention.toDate
import com.app.inails.booking.admin.model.response.CustomerDTO
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.Schedule
import com.app.inails.booking.admin.model.response.StaffDTO
import java.text.SimpleDateFormat
import java.util.*


@Inject(ShareScope.Singleton)
class TextFormatter {
    fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "").trim()
    }

    fun formatDisplayTimeZone(salonDTO: SalonDTO): String {
        return "Business hour (${salonDTO.timezone} ${salonDTO.tz})"
    }

    fun formatPhoneUS(phone: String?): String {
        return if (phone.isNullOrEmpty()) ""
        else PhoneNumberUtils.formatNumber(phone, "US")
    }

    fun fullName(staffDTO: StaffDTO) = "${staffDTO.first_name} ${staffDTO.last_name}"

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

    fun formatTextColorStaffColor(active: Int): Int {
        return if (active == 1) R.color.black else R.color.gray05
    }

    fun formatBackgroundServiceColor(active: Int): Int {
        return if (active == 1) R.color.white else R.color.gray11
    }

    fun formatStatusAppointmentIcon(status: Int?, type: Int): Int {
        return when (status) {
            DataConst.AppointmentStatus.APM_CANCEL -> R.drawable.circle_gray
            DataConst.AppointmentStatus.APM_ACCEPTED -> R.drawable.circle_blue
            DataConst.AppointmentStatus.APM_WAITING -> if (type == 1) R.drawable.circle_orange else R.drawable.circle_yellow
            DataConst.AppointmentStatus.APM_IN_PROCESSING -> R.drawable.circle_blue3
            DataConst.AppointmentStatus.APM_FINISH -> R.drawable.circle_green
            else -> R.drawable.circle_yellow
        }
    }

    fun formatStatusAppointmentColor(status: Int?, type: Int): Int {
        return when (status) {
            DataConst.AppointmentStatus.APM_CANCEL -> R.color.gray09
            DataConst.AppointmentStatus.APM_ACCEPTED -> R.color.lightBlue02
            DataConst.AppointmentStatus.APM_WAITING -> if (type == 1) R.color.orange else R.color.yellow
            DataConst.AppointmentStatus.APM_IN_PROCESSING -> R.color.lightBlue03
            DataConst.AppointmentStatus.APM_FINISH -> R.color.green
            else -> R.color.yellow
        }
    }

    fun formatFullAddress(salonDTO: SalonDTO): String {
        val result = StringBuffer()
        if (!salonDTO.address.isNullOrEmpty())
            result.append(salonDTO.address)
        if (!salonDTO.city.isNullOrEmpty())
            result.append(" ").append(salonDTO.city)
        if (!salonDTO.state.isNullOrEmpty())
            result.append(" ").append(salonDTO.state)
        if (!salonDTO.zipcode.isNullOrEmpty())
            result.append(" ").append(salonDTO.zipcode)
        return result.toString()
    }

    fun formatSalonSchedule(it: Schedule): String {
        return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) "Not open"
        else "${it.startTimeFormat} - ${it.endTimeFormat}"
    }

    fun formatStatusAppointmentCancel(cancelledBy: String?): String {
        if (!cancelledBy.isNullOrEmpty()) {
            return "Cancelled by $cancelledBy"
        }
        return ""
    }

    fun fullAddress(customerDTO: CustomerDTO) =
        if (customerDTO.address.isNullOrEmpty() && customerDTO.city.isNullOrEmpty()
            && customerDTO.state.isNullOrEmpty() && customerDTO.zip.isNullOrEmpty()
        )
            "No Information"
        else
            "${customerDTO.address ?: ""}, ${customerDTO.city ?: ""}, ${customerDTO.state ?: ""}, ${customerDTO.zip ?: ""}"

    fun getTimeEndAppointment(timeCheckIn: String?, workTime: Int?): String {
        if (timeCheckIn.isNullOrEmpty()) return ""
        if (workTime == null || workTime == 0) return ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date =
            dateFormat.parse(timeCheckIn)
        val timePlus = date.time + (workTime * 60 * 1000)
        date.time = timePlus
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun getDateTimeWithEndAppointment(
        dateTime: String,
        workTime: Int?
    ): String {
        if (workTime == null || workTime == 0) return dateTime.formatDateAppointment()
        val date = dateTime.toDate()
        val timePlus = date.time + (workTime * 60 * 1000)
        date.time = timePlus

        return "${dateTime.toDate().time.toCreatedAt()}-${date.formatDateAppointment()}"
    }

    fun formatColorNotification(isRead: Int): Int {
        return if (isRead == 1) R.color.gray19 else R.color.black
    }

}