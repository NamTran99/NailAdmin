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
    val servicesName: String get() = ""
    val serviceCustom: String get() = ""
    val workTime: Int get() = 0
    val status: Int get() = 0
    val resIconStatus: Int @DrawableRes get() = R.drawable.circle_yellow
    val colorStatus: Int @ColorRes get() = R.color.yellow
    val statusDisplay: String get() = ""
    val staffName: String get() = ""
    val type: Int get() = 0
    val canceledBy: String get() = ""
    val serviceList: List<IService> get() = listOf()
    val totalPrice: Double get() = 0.0
    val customer: ICustomer? get() = null
    val notes: String? get() = null
    val dateTime: String get() = ""
    val hasFeedback : Boolean get() = false
    val feedbackContent : String get() = ""
    val feedbackRating : Int get() = 0
    val noteFinish : String get() = ""
    val reasonCancel : String get() = ""
    val price: Double get() = 0.0
    val serviceCustomObj : IService? get() = null

}

@Parcelize
class AppointmentForm(
    override var phone: String = "",
    override var name: String = "",
    @SerializedName("date_appointment")
    override var dateAppointment: String = "",
    @SerializedName("staff_id")
    override var staffID: Int = 0,
    var services: String = "",
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
        if (workTime == 0) resourceError(R.string.error_empty_service_time)
    }
}

@Parcelize
class AppointmentStatusForm(
    var id: Int = 0,
    var status: Int = 0,
    var price: Double = 0.0,
    var note: String = ""
) : Parcelable

@Parcelize
class StartServiceForm(
    var id: Int = 0,
    var status: Int = 0,
    @SerializedName("staff_id")
    var staffId: Int = 0,
    @SerializedName("work_time")
    var workTime: Int = 0,
) : Parcelable

@Parcelize
class CancelAppointmentForm(
    var id: Int = 0,
    var reason: String = "",
) : Parcelable

@Parcelize
class HandleAppointmentForm(
    var id: Int = 0,
    @SerializedName("is_accepted")
    var isAccepted: Int = 0,
    @SerializedName("work_time")
    var workTime: Int = 0,
    var reason: String = "",
    @SerializedName("staff_id")
    var staffId: Int = 0,
) : Parcelable
