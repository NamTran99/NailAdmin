package com.app.inails.booking.admin.model.form

import android.net.Uri
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.viewError
import com.google.gson.annotations.SerializedName

class FeedBackForm(
    var rating: Int = 0,
    var content: String = "",
    @SerializedName("appointment_id")
    var appointmentID: Long = 0,
    var images : MutableList<Uri> = mutableListOf()
) {
    fun validate() {
        if (content.isBlank()) viewError(R.id.edtFbContents, R.string.error_blank_content)
        if (content.trim().length < 10) viewError(R.id.edtFbContents, R.string.error_length_content)
    }
}