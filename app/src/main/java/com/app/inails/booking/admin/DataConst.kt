package com.app.inails.booking.admin

import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.client.ITime
import com.app.inails.booking.admin.model.ui.client.ITimeImpl
import com.app.inails.booking.admin.model.ui.client.popup.INotificationItemOption
import com.app.inails.booking.admin.model.ui.client.popup.INotificationOption
import com.app.inails.booking.admin.views.clients.salon.SalonImageFragment


object DataConst {

    object AppointmentStatus {
        const val APM_PENDING = 1
        const val APM_WAITING = 2
        const val APM_IN_PROCESSING = 3
        const val APM_FINISH = 4
        const val APM_CANCEL = 5
        const val APM_DELETED = 6
        const val APM_ACCEPTED = 7
    }

    object TypeAppointment {
        const val TYPE_WALK_IN = 1
        const val TYPE_APPOINTMENT = 2
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

    object NotifyFireBaseCloudType {
        //
        const val CUSTOMER_CREATE_APPOINTMENT = 1
        const val CUSTOMER_CANCEL_APPOINTMENT = 11
        const val CUSTOMER_FEEDBACK = 14

        //
        const val OWNER_ACCOUNT_APPROVE = 15
        const val OWNER_ACCOUNT_ACTIVE = 16
        const val OWNER_ACCOUNT_INACTIVE = 17
    }

    object GuidanceType {
        const val IMAGE = "image"
        const val VIDEO = "video"
    }

    object VoucherType {
        const val TYPE_PERCENT = 1
        const val TYPE_VALUE = 2
    }

    object Notification {
        const val BOOKING_FINISHED = "3"
        const val BOOKING_CANCELLED = "4"
        const val BOOKING_OWNER_CREATED = "6"
        const val BOOKING_OWNER_UPDATE = "7"
        const val BOOKING_ACCEPTED = "9"
        const val BOOKING_REJECTED = "10"
        const val BOOKING_REMINDED = "12"
    }

    object Mock {
        val notificationOptions = listOf(
            object : INotificationOption {
                override val id: Int
                    get() = 1
                override val value: String
                    get() = "Read All"

                override fun toString(): String {
                    return value
                }
            }, object : INotificationOption {
                override val id: Int
                    get() = 2
                override val value: String
                    get() = "Delete All"

                override fun toString(): String {
                    return value
                }
            }
        )
        val notificationItemUnreadOptions = listOf(
            object : INotificationItemOption {
                override val id: Int
                    get() = 2
                override val value: String
                    get() = "Mark as unread"

                override fun toString(): String {
                    return value
                }
            },
            object : INotificationItemOption {
                override val id: Int
                    get() = 3
                override val value: String
                    get() = "Delete"

                override fun toString(): String {
                    return value
                }
            }
        )
        val notificationItemReadOptions = listOf(
            object : INotificationItemOption {
                override val id: Int
                    get() = 1
                override val value: String
                    get() = "Mark as read"

                override fun toString(): String {
                    return value
                }
            },
            object : INotificationItemOption {
                override val id: Int
                    get() = 3
                override val value: String
                    get() = "Delete"

                override fun toString(): String {
                    return value
                }
            }
        )
        val times = listOf<ITime>(
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 1
                override val time: String
                    get() = "09:00"
                override val timeDisplay: String
                    get() = "09:00 AM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 2
                override val time: String
                    get() = "09:30"
                override val timeDisplay: String
                    get() = "09:30 AM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 3
                override val time: String
                    get() = "10:00"
                override val timeDisplay: String
                    get() = "10:00 AM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 4
                override val time: String
                    get() = "10:30"
                override val timeDisplay: String
                    get() = "10:30 AM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 5
                override val time: String
                    get() = "15:00"
                override val timeDisplay: String
                    get() = "03:00 PM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 6
                override val time: String
                    get() = "15:30"
                override val timeDisplay: String
                    get() = "03:30 PM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 7
                override val time: String
                    get() = "16:00"
                override val timeDisplay: String
                    get() = "04:00 PM"
            },
            object : ITime, ISelector by ITimeImpl() {
                override val id: Int
                    get() = 8
                override val time: String
                    get() = "16:30"
                override val timeDisplay: String
                    get() = "04:30 PM"
            }
        )
    }

    object Tab {
        fun gallery(imageBefore: List<String>, imageAfter: List<String>) =
            listOf(
                SalonImageFragment(imageBefore),
                SalonImageFragment(imageAfter)
            )
    }
}
