package com.app.inails.booking.admin.model.ui.client

import android.os.Build
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.isEmail
import com.google.gson.annotations.SerializedName

interface IUserClient {
    val phone: String get() = ""
    val email: String get() = ""
    val address: String get() = ""
    val dob: String get() = ""
    val welcomeName: String get() = ""
    val fullName: String get() = ""
}

interface IUserClientEdit : IUserClient {
    val emailEditable: String get() = ""
    val addressEditable: String get() = ""
    val dobEditable: String get() = ""
    val name: String get() = ""
}

class LoginForm(
    override var phone: String = "",
    var password: String = "",
    @SerializedName("device_type")
    val deviceType: String = "android",
    @SerializedName("device_info")
    val deviceInfo: String = Build.MANUFACTURER + "-" + Build.MODEL,
    @SerializedName("device_token")
    var pushToken: String = ""
) : IUserClient {
    fun validate() {
//        if (phone.isBlank() || phone.length < 14) viewError(
//            R.id.edtPhone,
//            R.string.error_blank_phone
//        )
//        if (password.isBlank()) viewError(R.id.edtPassword, R.string.error_blank_password)
//        if (password.trim().length < 6) viewError(R.id.edtRgPassword, R.string.error_valid_password)
    }
}

class RegisterForm(
    override var phone: String = "",
    @SerializedName("salon_slug")
    var salonSlug: String = "",
    var name: String = "",
    var password: String = "",
    override var email: String = "",
    override var address: String = "",
    @SerializedName("device_type")
    val deviceType: String = "android",
    @SerializedName("device_info")
    val deviceInfo: String = Build.MANUFACTURER + "-" + Build.MODEL,
    @SerializedName("birthday")
    override var dob: String = "",
    @SerializedName("device_token")
    var pushToken: String = ""
) : IUserClient {
    fun validate() {
//        if (phone.isBlank() || phone.length < 14) viewError(
//            R.id.edtRgPhone,
//            R.string.error_blank_phone
//        )
//        if (password.isBlank()) viewError(R.id.edtRgPassword, R.string.error_blank_password)
//        if (password.trim().length < 6) viewError(R.id.edtRgPassword, R.string.error_valid_password)
//        if (name.isBlank()) viewError(R.id.edtRgName, R.string.error_blank_name)
//        if (email.isNotBlank() && !email.isEmail()) viewError(
//            R.id.edtRgEmail,
//            R.string.error_not_correct_email
//        )
    }
}

class UpdateProfileForm(
    override var name: String = "",
    override var phone: String = "",
    override var email: String = "",
    override var address: String = "",
    @SerializedName("birthday")
    override var dob: String = "",
    var dobOrigin: String? = ""
) : IUserClientEdit {

    fun validate() {
        if (phone.isBlank() || phone.length < 14) viewError(
            R.id.edtPfPhone,
            R.string.error_blank_phone
        )
        if (email.isNotEmpty() && !email.isEmail()) viewError(
            R.id.edtPfEmail,
            R.string.error_not_correct_email
        )
    }
}