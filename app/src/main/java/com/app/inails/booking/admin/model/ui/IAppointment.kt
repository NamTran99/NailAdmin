package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IAppointment {
    val id: Int get() = 0
    val phone: String get() = ""
    val name: String get() = ""
    val dateAppointment: String get() = ""
    val staffID: Int get() = 0
    val services: String get() = ""
    val serviceCustom: String get() = ""
    val workTime: Int get() = 0
    val status: Int get() = 0
    val resIconStatus: Int @DrawableRes get() = R.drawable.circle_yellow
    val colorStatus: Int @ColorRes get() = R.color.yellow
    val statusDisplay: String get() = ""
    val staffName: String get() = ""
}

@Parcelize
class AppointmentForm(
    override var phone: String = "",
    override var name: String = "",
    @SerializedName("date_appointment")
    override var dateAppointment: String = "",
    @SerializedName("staff_id")
    override var staffID: Int = 0,
    override var services: String = "",
    @SerializedName("service_custom")
    override var serviceCustom: String = "",
    @SerializedName("work_time")
    override var workTime: Int = 0,
    var hasServiceCustom: Boolean = false,
    var hasStaff: Boolean = true
) : IAppointment, Parcelable {

    fun validate() {
        if (phone.isBlank() || phone.length < 14) viewError(
            R.id.etPhone,
            R.string.error_blank_phone
        )
        if (name.isBlank()) viewError(R.id.etFullName, R.string.error_blank_name)
        if (staffID == 0 && hasStaff) resourceError(R.string.error_blank_staff_id)
        if (dateAppointment.isBlank()) resourceError(R.string.error_blank_date_time)
        if (services.isBlank() || services.isEmpty()) resourceError(R.string.error_empty_services)
        if (hasServiceCustom && serviceCustom.isBlank()) resourceError(R.string.error_empty_service_custom)
        if (workTime == -1) resourceError(R.string.error_empty_total_duration_services)
    }
}