package com.app.inails.booking.admin.model.form

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.google.gson.annotations.SerializedName

class BookingForm(
    @SerializedName("salon_slug")
    var salonSlug: String = "",
    @SerializedName("staff_id")
    var staffId: Int? = 0,
    var services: String = "",
    var note: String = "",
    @SerializedName("date_appointment")
    var datetime: String = "",
    var type: Int = TYPE_APPOINTMENT,//1 : walk-in || 2: appointment
    var hasStaff: Boolean = true,
    var voucher: String = ""
) {
    companion object {
        const val TYPE_WALK_IN = 1
        const val TYPE_APPOINTMENT = 2
    }

    fun validate() {
        if ((staffId == null || staffId == 0) && hasStaff) resourceError(R.string.error_blank_staff)
    }
}