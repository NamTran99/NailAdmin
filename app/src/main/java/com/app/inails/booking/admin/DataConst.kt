package com.app.inails.booking.admin

import com.app.inails.booking.admin.views.booking.AppointmentFragment


object DataConst {
    object AppointmentTab {
        val appointment = listOf(
            R.string.title_walk_in_customer to AppointmentFragment(1),
            R.string.title_appointment_customer to AppointmentFragment(2)
        )
    }

    object AppointmentStatus {
        const val APM_PENDING = 1
        const val APM_WAITING = 2
        const val APM_IN_PROCESSING = 3
        const val APM_FINISH = 4
        const val APM_CANCEL = 5
        const val APM_DELETED = 6
    }

    object StaffStatus {
        const val STAFF_AVAILABLE = 1
        const val STAFF_BREAK = 2
        const val STAFF_WORKING = 3
    }

    object ChangeStaffStatus {
        const val CHECK_IN = 1
        const val CHECK_OUT = 2
        const val WORKING = 3
    }

}