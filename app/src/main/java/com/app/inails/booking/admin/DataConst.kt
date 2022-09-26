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
        const val APM_ACCEPTED = 7
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

    object ChangeApmStatus {
        const val WAITING = 1
        const val IN_PROCESSING = 2
        const val FINISHED = 3
        const val CANCEL = 4
    }

    object CancelAppointmentBy {
        const val ADMIN_SALON = 1
        const val CLIENT = 2
    }

    object NotifyFireBaseCloudType{
        const val CUSTOMER_CREATE_APPOINTMENT = 1
        const val CUSTOMER_CANCEL_APPOINTMENT = 11
        const val CUSTOMER_FEEDBACK = 14
    }

}