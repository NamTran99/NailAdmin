package com.app.inails.booking.admin.model.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IStaff {
    val id: Int get() = 0
    val name: String get() = ""
    val firstName: String get() = ""
    val lastName: String get() = ""
    val phone: String get() = ""
    val status: Int get() = 0
    val resIconStatus: Int @DrawableRes get() = R.drawable.circle_yellow
    val statusName: String get() = ""
    val colorStatus: Int @ColorRes get() = R.color.yellow
    val textColor: Int @ColorRes get() = R.color.black
    val timeCheckIn: String get() = ""
    val timeCheckInAppointment: String get() = ""
    val timeEndAppointment: String get() = ""
    val active: Int get() = 0
    val customerName: String get() = ""
    val appointment: IAppointment? get() = null
    val orderStaffStatusList: Int? get()= 0
}

@Parcelize
class StaffForm(
    override var id: Int = 0,
    override var phone: String = "",
    override var name: String = ""
) : IStaff, Parcelable {
    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable("staff", this)
        return bundle
    }
}


@Parcelize
class UpdateStatusStaffForm(
    var id: Int = 0,
    var status: Int = 0

) : Parcelable

@Parcelize
class StaffImpl : IStaff, ISelector, Parcelable {
    override var isSelector: Boolean = false
}

@Parcelize
class UpdateStaffForm(
    override var id: Int = 0,
    override var phone: String = "",
    @SerializedName("first_name")
    override var firstName: String = "",
    @SerializedName("last_name")
    override var lastName: String = ""
) : IStaff, Parcelable {

    fun validate() {
        if (phone.isBlank() || phone.length < 14) resourceError(
            R.string.error_blank_phone
        )
        if (firstName.isBlank()) resourceError(R.string.error_blank_firstname)
        if (lastName.isBlank()) resourceError(R.string.error_blank_lastname)
    }
}


@Parcelize
class CreateStaffForm(
    override var phone: String = "",
    @SerializedName("first_name")
    override var firstName: String = "",
    @SerializedName("last_name")
    override var lastName: String = ""
) : IStaff, Parcelable {

    fun validate() {
        if (phone.isBlank() || phone.length < 14) resourceError(
            R.string.error_blank_phone
        )
        if (firstName.isBlank()) resourceError(R.string.error_blank_firstname)
        if (lastName.isBlank()) resourceError(R.string.error_blank_lastname)
    }
}

