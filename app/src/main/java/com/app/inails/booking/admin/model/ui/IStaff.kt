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
    val avatar: String? get() = null
    val description: String get() = ""
    val is_delete_avatar: Int  get() = 0
    val isAvatarDefault: Boolean get() = false
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

class UpdateStaffForm(
    override var id: Int = 0,
    override var phone: String = "",
    @SerializedName("first_name")
    override var firstName: String = "",
    @SerializedName("last_name")
    override var lastName: String = "",
    override var description: String = "",
    override var avatar: String? = null,
    override var is_delete_avatar: Int = 0
) : IStaff {

    fun validate() {
        if (firstName.isBlank()) resourceError(R.string.error_blank_firstname)
        if (lastName.isBlank()) resourceError(R.string.error_blank_lastname)
        if (phone.isBlank()) resourceError(
            R.string.error_blank_phone
        )
    }
}


class CreateStaffForm(
    override var phone: String = "",
    @SerializedName("first_name")
    override var firstName: String = "",
    @SerializedName("last_name")
    override var lastName: String = "",
    override var description: String=  "",
    override var avatar: String? = null
) : IStaff {

    fun validate() {
        if (firstName.isBlank()) resourceError(R.string.error_blank_firstname)
        if (lastName.isBlank()) resourceError(R.string.error_blank_lastname)
        if (phone.isBlank()) resourceError(
            R.string.error_blank_phone
        )
    }
}

