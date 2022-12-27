package com.app.inails.booking.admin.model.ui.client

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.ui.ISalonDetail

interface IAppointmentClient : IBooking, ISalonDetailClient {
    val id: Long get() = 0
    val idDisplay: String get() = ""
    val serviceDisplay: String get() = ""
    val status: Triple<Int, Int, Int>
        // text/color/icon
        get() = Triple(
            R.string.status_appointment_waiting,
            R.color.yellow,
            R.drawable.ic_oval_yellow
        )
    val isShowCancelButton: Boolean get() = false
    val isShowNote: Boolean get() = false
    val isShowCancelNote: Boolean get() = false
    val canceledBy: String get() = ""
    val reasonCancel: String get() = ""
    val isFinish: Boolean get() = false
    val finishNote: String get() = ""
    val hasFeedBack: Boolean get() = false
    val feedbackContent: String get() = ""
    val feedbackRating: Float get() = 0f
    val isShowDirect: Boolean get() = false
    val createAtDisplay: String get() = ""
    val feedbackImages: List<String>? get() = listOf()
}

interface StatusEditable {
    fun setCancelStatus(reason: String)
}