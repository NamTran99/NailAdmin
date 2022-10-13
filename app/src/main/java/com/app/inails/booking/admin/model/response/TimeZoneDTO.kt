package com.app.inails.booking.admin.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

data class TimeZoneDTO(
    val dstOffset: Int = 0,
    val rawOffset: Int = 0,
    val status: String = "",
    val timeZoneId: String= "",
    val timeZoneName: String = ""
)

@Parcelize
class TimeZoneForm(
    var location: String = "",
    var timestamp: Long = 0,
    var key: String= ""
) : Parcelable