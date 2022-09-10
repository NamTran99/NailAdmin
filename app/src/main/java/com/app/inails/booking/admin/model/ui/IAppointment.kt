package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IAppointment {
    val phone: String get() = ""
    val name: String get() = ""
    val dateAppointment: String get() = ""
    val staffID: Int get() = 0
    val services: MutableList<Int> get() = mutableListOf()
    val serviceCustom: String get() = ""
    val workTime: String get() = ""
}

@Parcelize
class AppointmentForm(
    override var phone: String = "",
    override var name: String = "",
    @SerializedName("date_appointment")
    override var dateAppointment: String = "",
    @SerializedName("staff_id")
    override var staffID: Int = 0,
    override var services: MutableList<Int> = mutableListOf(),
    @SerializedName("service_custom")
    override var serviceCustom: String = "",
    @SerializedName("work_time")
    override var workTime: String = "",
    @Expose
    var hasServiceCustom: Boolean = false
) : IAppointment, Parcelable {

    fun validate() {
        if (phone.isBlank() || phone.length < 14) viewError(
            R.id.etPhone,
            R.string.error_blank_phone
        )
        if (name.isBlank()) viewError(R.id.etFullName, R.string.error_blank_name)
        if (staffID == 0) resourceError(R.string.error_blank_staff_id)
        if (dateAppointment.isBlank()) resourceError(R.string.error_blank_date_time)
        if (services.isEmpty()) resourceError(R.string.error_empty_services)
        if (hasServiceCustom && serviceCustom.isBlank()) resourceError(R.string.error_empty_service_custom)
        if (workTime.isBlank()) resourceError(R.string.error_empty_total_duration_services)
    }
}