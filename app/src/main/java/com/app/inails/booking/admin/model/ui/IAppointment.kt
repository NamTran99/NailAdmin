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
    val customerName: String get() = ""
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
    val totalPrice: Double get() = 0.0 // là total price service
    val customer: ICustomer? get() = null
    val staff: IStaff? get() = null
    val notes: String get() = ""
    val dateTime: String get() = ""
    val hasFeedback: Boolean get() = false
    val feedbackContent: String get() = ""
    val feedbackRating: Float get() = 0f
    val noteFinish: String get() = ""
    val reasonCancel: String get() = ""
    val totalPriceService: String get() =""
    val serviceCustomObj: IService? get() = null
    val createAt: String get() = ""
    val dateSelected: String get() = ""
    val timeSelected: String get() = ""
    val dateTag: String get() = ""
    val customerID: Int get() = 0
    val feedbackImages: List<AppImage> get() = listOf()
    val afterImage: List<AppImage> get() = listOf()
    val beforeImage: List<AppImage> get() = listOf()
    val hasVoucher: Boolean get() = false
    val percent: String get() = ""
    val showPercent : Boolean get() =false
    val discount: String get() = ""
    val totalAmount: String get() = ""
    val voucherCode: String get() = ""
    val voucher: IVoucher? get() = null
}

@Parcelize
class AppointmentForm(
    var id: Int? = null,
    var phone: String = "",
    var name: String = "",
    @SerializedName("date_appointment")
    var dateAppointment: String = "",
    @SerializedName("staff_id")
    var staffID: Int = 0,
    var services: String = "",
    @SerializedName("service_custom")
    var serviceCustom: String = "",
    @SerializedName("work_time")
    var workTime: Int = 0,
    var hasStaff: Boolean = true,
    var note: String = ""
) : Parcelable {

    fun validate() {
        if (phone.isBlank()) viewError(
            R.id.etPhone,
            R.string.error_blank_phone
        )
        if (name.isBlank()) viewError(R.id.etFullName, R.string.error_blank_name)
        if (staffID == 0 && hasStaff) resourceError(R.string.error_blank_staff_id)
        if (dateAppointment.isBlank()) resourceError(R.string.error_blank_date_time)
        if (services.isBlank() || services.isEmpty()) resourceError(R.string.error_empty_services)
        if (workTime == 0) resourceError(R.string.error_empty_service_time)
    }
}

class AppointmentStatusForm(
    var id: Int = 0,
    var status: Int = 0,
    var price: Double = 0.0,
    var note: String = "",
    var beforeImages: List<AppImage>  = listOf(),
    var afterImages: List<AppImage> = listOf()
)

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


class AppointmentFilterForm(
    @SerializedName("to_date")
    var toDate: String? = null,
    @SerializedName("date")
    var fromDate: String? = null,
    @SerializedName("search_staff")
    var searchStaff: Int? = null,
    @SerializedName("search_customer")
    var searchCustomer: Int? = null,
    var staff : IStaff ?= null,
    var customer : ICustomer ?= null,
    var keyword: String = "",
    var status: Int? = null,
    var type: Int? = 1, // type = null: filter type 1 && 2
){
    fun isHaveDataForFilter(): Boolean{
        return  !fromDate.isNullOrEmpty() || !toDate.isNullOrEmpty() ||
                staff != null || customer != null
                || status != null
    }

    fun setDataFromDialog(form: AppointmentFilterForm){
        toDate = form.toDate
        fromDate = form.fromDate
        staff = form.staff
        customer = form.customer
        searchStaff = form.staff?.id ?:form.searchStaff
        searchCustomer = form.customer?.id ?: form.searchCustomer
        status = form.status
    }
}
