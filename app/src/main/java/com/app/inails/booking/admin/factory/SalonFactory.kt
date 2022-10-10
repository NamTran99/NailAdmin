package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.extention.toTimeCheckIn
import com.app.inails.booking.admin.extention.toTimeEditSchedule
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.Schedule
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class SalonFactory(private val textFormatter: TextFormatter) {

    private fun create(salonDTO: SalonDTO): ISalon {
        return object : ISalon, ISalonDTOOwner {
            override val salonName: String
                get() = salonDTO.name.safe()
            override val slug: String
                get() = salonDTO.slug.safe()
            override val value: SalonDTO get() = salonDTO
            override val address: String
                get() = textFormatter.formatFullAddress(salonDTO)
            override val phoneNumber: String
                get() = salonDTO.phone.safe()
            override val rating: Float
                get() = salonDTO.rating.safe()
        }
    }

    fun createSchedule(schedules: List<Schedule>?): List<ISchedule>? {
        return schedules?.map {
            ISchedule(
                day = it.day,
                timeFormat = textFormatter.formatSalonSchedule(it),
                startTimeFormat = it.startTimeFormat,
                endTimeFormat = it.endTimeFormat,
                startTime = it.startTime.toTimeEditSchedule(),
                endTime = it.endTime.toTimeEditSchedule(),
                dayFormat = it.dayName
            )
        }
    }

    fun createDetail(salonDTO: SalonDTO): ISalonDetail {
        return object : ISalonDetail, ISalon by create(salonDTO) {
            override val ownerName: String
                get() = salonDTO.partner?.name.safe()
            override val des: String
                get() = salonDTO.description.safe()
            override val lat: Float
                get() = salonDTO.lat
            override val lng: Float
                get() = salonDTO.lng
            override val images: List<SalonImage>
                get() = salonDTO.images?.map { SalonImage(it.id.toInt(), it.image) } ?: listOf()
            override val schedules: List<ISchedule>?
                get() = createSchedule(salonDTO.schedules)
        }
    }
}