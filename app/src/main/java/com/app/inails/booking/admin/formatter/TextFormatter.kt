package com.app.inails.booking.admin.formatter

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.annotation.StringRes
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_ACCEPTED
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_CANCEL
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_DELETED
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_FINISH
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_IN_PROCESSING
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_PENDING
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_WAITING
import com.app.inails.booking.admin.DataConst.StaffStatus.STAFF_AVAILABLE
import com.app.inails.booking.admin.DataConst.StaffStatus.STAFF_WORKING
import com.app.inails.booking.admin.DataConst.TypeAppointment.TYPE_APPOINTMENT
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.response.client.*
import com.esafirm.imagepicker.helper.LocaleManager
import java.text.SimpleDateFormat
import java.util.*


@Inject(ShareScope.Singleton)
class TextFormatter(private val cxt: Context, val userLocalSource: UserLocalSource) {

    private fun getLanguageContext() =
        LocaleManager.updateResources(cxt, userLocalSource.getLanguageWithDefault())

    private fun getString(@StringRes id: Int) = getLanguageContext().getString(id)
    private fun getString(@StringRes id: Int, vararg format: Any) =
        getLanguageContext().getString(id, *format)

    fun yesNoFormat(indx: Int): String {
        return when (indx) {
            0 -> getString(R.string.no)
            1 -> getString(R.string.yes)
            else -> getString(R.string.no)
        }
    }


    //Job Profile
    fun formatDistance(distance: Double): String {
        return getLanguageContext().getString(R.string.distance_format_1, distance.showMaxNumber(2))
    }

    fun formatWorkplaceType(indx: Int): String {
        return getString(
            when (indx) {
                1 -> R.string.local_position
                2 -> R.string.willing_to_relocate
                else -> R.string.local_position
            }
        )
    }

    fun formatExperienceJob(number: Double): String {
        val numDis = displayNumber(number)
        return if (number > 1) getLanguageContext().getString(
            R.string.format_experience_plural,
            numDis
        ) else if (number == 0.0) getLanguageContext().getString(
            R.string.no_experience
        )
        else getLanguageContext().getString(R.string.format_experience, numDis)
    }

    fun formatExperienceJobEn(number: Double): String {
        val numDis = displayNumber(number)
        return if (number > 1) getLanguageContext().getString(
            R.string.format_experience_plural_1,
            numDis
        ) else if (number == 0.0) getLanguageContext().getString(
            R.string.no_experience_en
        )
        else getLanguageContext().getString(R.string.format_experience_en, numDis)
    }

    fun formatExperienceRecruitment(number: Double): String {
        val numDis = displayNumber(number)
        return if (number > 1) getLanguageContext().getString(
            R.string.format_experience_plural_2,
            numDis
        ) else if (number == 0.0) getLanguageContext().getString(R.string.not_required)
        else getLanguageContext().getString(R.string.format_experience_2, numDis)
    }

    fun formatPayrollType(indx: Int): String {
        return getString(
            when (indx) {
                1 -> R.string.full_salary
                2 -> R.string.split46
                3 -> R.string.negotiate
                else -> R.string.full_salary
            }
        )
    }

    private fun displayNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            String.format("%.0f", number)
        } else {
            number.toString()
        }
    }

    fun formatGender(gender: Int): String {
        return when (gender) {
            1 -> getLanguageContext().getString(R.string.gender_male)
            2 -> getLanguageContext().getString(R.string.gender_female)
            else -> getLanguageContext().getString(R.string.gender_all_2)
        }
    }

    fun formatGender2(gender: Int): String {
        return when (gender) {
            1 -> getLanguageContext().getString(R.string.format_gender_male)
            2 -> getLanguageContext().getString(R.string.format_gender_female)
            else -> getLanguageContext().getString(R.string.format_gender_other)
        }
    }

    //recruitment
    private fun getLocalString(@StringRes id: Int): String {
        return getLanguageContext().getString(id)
    }

    fun customerSkinColorFormat(indx: Int): String {
        return getString(
            when (indx) {
                1 -> R.string.skin_white
                2 -> R.string.skin_black
                3 -> R.string.skin_xi
                4 -> R.string.skin_all
                else -> R.string.skin_white
            }
        )
    }

    fun formatWorkingTime(workingForm: Int, workingTerm: Int): String {
        val format = if (workingForm == 2) {
            getLocalString(R.string.full_time)
        } else {
            if (workingTerm > 1) {
                getLanguageContext().getString(R.string.working_time_format_2, workingTerm)
            } else {
                getLanguageContext().getString(R.string.working_time_format, workingTerm)
            }
        }
        return format
    }

    fun formatPositionRecruitment(position: Int, other: String): String {
        if (other.isNotEmpty()) return other
        return when (position) {
            1 -> getLocalString(R.string.manager)
            2 -> getLocalString(R.string.main_manicurist)
            3 -> getLocalString(R.string.assistant_manicurist)
            else -> getLocalString(R.string.manager)
        }
    }

    fun formatSalary(salary: Double, type: Int): String {
           val value = salary.display()
        val suffix = when (type) {
            1 -> getLanguageContext().getString(R.string.per_week_1)
            2 -> getLanguageContext().getString(R.string.per_month)
            else -> getLanguageContext().getString(R.string.per_year)
        }
        return "$$value $suffix"
    }

    fun formatSalaryRecruitmentEn(salary: Double, type: Int): String {
        val value = salary.display()
        val suffix = when (type) {
            1 -> getLanguageContext().getString(R.string.per_week_en)
            2 -> getLanguageContext().getString(R.string.per_month_en)
            else -> getLanguageContext().getString(R.string.per_year_en)
        }
        return "$$value $suffix"
    }

    fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "").trim()
    }

    fun formatDisplayTimeZone(salonDTO: SalonDTO): String {
        return getLanguageContext().getString(
            R.string.business_hour_format,
            salonDTO.timezone,
            salonDTO.tz
        )
    }

    fun displaySafe(text: String?): String {
        return (text ?: "").ifBlank { getLanguageContext().getString(R.string.no_information) }
    }

    fun formatPhoneUS(phone: String?): String {
        return if (phone.isNullOrEmpty()) ""
        else phone.formatPhoneUSCustom()
    }

    fun fullName(staffDTO: StaffDTO) = "${staffDTO.first_name} ${staffDTO.last_name}"
    fun fullName(staffDTO: StaffClientDTO) = "${staffDTO.first_name} ${staffDTO.last_name}"
    fun formatBookingID(id: Long?) = "#$id"
    fun colorTextNotification(isRead: Int?) = if (isRead == 0) R.color.black else R.color.gray_188

    fun titleNotificationWithUnread(totalUnread: Int): String {
        return if (totalUnread == 0) getLanguageContext().getString(R.string.title_notifications)
        else getLanguageContext().getString(
            R.string.title_notifications_with_total,
            totalUnread.toString()
        )
    }

    fun statusStaff(status: Int?): Pair<Int, Int> {
        return when (status) {
            STAFF_WORKING -> R.string.status_staff_busy to R.drawable.ic_oval_red
            STAFF_AVAILABLE -> R.string.status_staff_available to R.drawable.ic_oval_green
            else -> R.string.status_staff_offline to R.drawable.ic_oval_yellow
        }
    }

    fun displayIDAppointment(id: Long?): String = "ID: #$id"

    fun statusAppointment(status: Int?): Triple<Int, Int, Int> {
        return when (status) {
            APM_PENDING -> Triple(
                R.string.status_appointment_pending,
                R.color.yellow,
                R.drawable.ic_oval_yellow
            )
            APM_WAITING -> Triple(
                R.string.status_appointment_waiting,
                R.color.orange_255,
                R.drawable.ic_oval_orange
            )
            APM_ACCEPTED -> Triple(
                R.string.status_appointment_accepted,
                R.color.blue_70,
                R.drawable.ic_oval_blue
            )
            APM_IN_PROCESSING -> Triple(
                R.string.status_appointment_in_processing,
                R.color.blue_70,
                R.drawable.ic_oval_blue
            )
            APM_FINISH -> Triple(
                R.string.status_appointment_finished,
                R.color.green,
                R.drawable.ic_oval_green
            )
            APM_CANCEL -> Triple(
                R.string.status_appointment_cancelled,
                R.color.gray_200,
                R.drawable.ic_oval_gray
            )
            APM_DELETED -> Triple(
                R.string.status_appointment_deleted,
                R.color.red_240,
                R.drawable.ic_oval_red
            )

            else -> Triple(
                R.string.status_appointment_unknown,
                R.color.red_240,
                R.drawable.ic_oval_red
            )
        }
    }

    fun isCancelAppointment(booking: BookingClientDTO) =
        booking.status == APM_PENDING && booking.type == TYPE_APPOINTMENT


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
        return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) getLanguageContext().getString(
            R.string.not_open
        )
        else "${it.startTimeFormat} - ${it.endTimeFormat}"
    }

    fun formatSalonScheduleClient(it: Schedule): String {
        return if (it.startTime.isNullOrEmpty() || it.startTime.isNullOrEmpty()) "OFF"
        else "${it.startTime} - ${it.endTime}"
    }

    fun formatBusinessHours(salonDTO: SalonDTO?, isDownLine: Boolean = false): String {
        val timezone = "${if (isDownLine) "\n" else ""}(${salonDTO?.timezone} ${salonDTO?.tz})"
        return cxt.getString(R.string.btn_business_hour, timezone)
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
            getLanguageContext().getString(R.string.no_information)
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

        return "${dateTime.toDate().time.convertFromServerToLocalTime()}-${date.formatDateAppointment()}"
    }

    fun formatColorNotification(isRead: Int): Int {
        return if (isRead == 1) R.color.gray19 else R.color.black
    }

    fun formatTotalFeedback(it: FeedbackClientDTO) =
        getLanguageContext().getString(
            R.string.label_count_rating,
            it.average_rating ?: "0",
            it.total ?: "0"
        )

    fun formatWelcomeUser(user: UserClientDTO.Data?): String {
        return if (user == null) cxt.getString(R.string.title_welcome_guest)
        else cxt.getString(R.string.label_welcome_with, user.name)
    }

    fun noInfo(email: String?): String {
        return if (email.isNullOrEmpty()) "No info"
        else email
    }

    fun formatSalonPrice(serviceDTO: ServiceClientDTO?) = formatPrice(serviceDTO?.price)

    fun formatPrice(price: Float?): String {
        return String.format("$%.2f", price).replace(",", ".")
    }

    fun formatPrice(price: Double?): String {
        return String.format("$%.2f", price).replace(",", ".")
    }

    fun canceledBy(cancelledBy: String? = "", isGuest: Boolean = false): String {
        if (isGuest) return cxt.getString(R.string.text_cancelled_by_guest)
        if (!cancelledBy.isNullOrEmpty()) {
            return cxt.getString(R.string.text_cancelled_by, cancelledBy)
        }
        return ""
    }

    fun formatPriceDiscount(total: Float, voucherDTO: VoucherClientDTO): String {
        return if (voucherDTO.type == DataConst.VoucherType.TYPE_VALUE) {
            "-${formatPrice(voucherDTO.value)}"
        } else {
            val price = (total * (voucherDTO.value / 100))
            "-${formatPrice(price)}"
        }
    }

    fun formatPercent(percent: Float): String {
        return "-${percent.toInt()}%"
    }

    fun formatPercent(percent: Double): String {
        return "-${percent.toInt()}%"
    }

    fun formatTotalAmount(total: Float, voucherDTO: VoucherClientDTO): String {
        val discount = if (voucherDTO.type == DataConst.VoucherType.TYPE_VALUE) {
            voucherDTO.value
        } else {
            total * (voucherDTO.value / 100)
        }
        return if (total - discount > 0)
            formatPrice(total - discount) else formatPrice(0f)
    }

    fun updateImageForService(serviceDTO: ServiceClientDTO): List<String>? {
        if (serviceDTO.image.isNullOrEmpty() && serviceDTO.images.isNullOrEmpty()) {
            return null
        }
        val imageList = mutableListOf<String>()
        imageList.add(serviceDTO.image ?: "")
        serviceDTO.images?.map { it.image }?.let { imageList.addAll(it) }
        return imageList
    }
}