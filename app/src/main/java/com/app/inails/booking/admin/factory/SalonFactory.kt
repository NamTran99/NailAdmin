package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.DataConst.VoucherType.TYPE_VALUE
import com.app.inails.booking.admin.app.AppConfig
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.Schedule
import com.app.inails.booking.admin.model.response.VoucherDTO
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
                get() = salonDTO.address.displaySafe1()
            override val phoneNumber: String
                get() = salonDTO.phone.safe().formatUsNumberCustom()
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
            override val salonID: Long
                get() = salonDTO.id
            override val email: String
                get() = salonDTO.email.safe()
            override val state: String
                get() = salonDTO.state.safe()
            override val city: String
                get() = salonDTO.city.safe()
            override val zipCode: String
                get() = salonDTO.zipcode.safe()
            override val country: String
                get() = salonDTO.country.safe()
            override val ownerName: String
                get() = salonDTO.partner?.name.safe()
            override val des: String
                get() = salonDTO.description.safe()
            override val lat: Float
                get() = salonDTO.lat
            override val lng: Float
                get() = salonDTO.lng
            override val images: List<AppImage>
                get() = salonDTO.images?.map { AppImage(it.id.toInt(), it.image) } ?: listOf()
            override val schedules: List<ISchedule>?
                get() = createSchedule(salonDTO.schedules)
            override val tzDisplay1: String
                get() = textFormatter.formatDisplayTimeZone(salonDTO)
            override val tzDisplay2: String
                get() = "${salonDTO.timezone} ${salonDTO.tz}"
            override val zoneID: String
                get() = salonDTO.timezone.safe()
            override val zoneOffSet: String
                get() = salonDTO.tz.safe()
            override val vouchers: List<IVoucher>
                get() = salonDTO.vouchers.map(::createVoucher)
            override val afterGalleryImage: List<AppImage>
                get() = salonDTO.gallery.filter { it.type == 2 }.map { AppImage(id = it.id, path = it.image?:"") }
            override val beforeGalleryImage: List<AppImage>
                get() = salonDTO.gallery.filter { it.type == 1 }.map { AppImage(id = it.id, path = it.image?:"") }

        }
    }

    fun createVoucher(voucherDto: VoucherDTO): IVoucher{
        return object : IVoucher{
            override val code: String
                get() = voucherDto.code.safe()
            override val type: VoucherType
                get() =
                    when(voucherDto.type){
                        1 -> VoucherType.PERCENT
                        else -> VoucherType.VALUE
                    }
            override val endDate: String
                get() = voucherDto.expiration_date.toVoucherTIme()
            override val startDate: String
                get() = voucherDto.start_date.toVoucherTIme()
            override val typeCustomer: String
                get() = when(voucherDto.type_customer){
                    1 ->  "All"
                    2 -> "Normal"
                    else -> "Vip"
                }
            override val value: Double
                get() = voucherDto.value
        }
    }


}