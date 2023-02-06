package com.app.inails.booking.admin.factory

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.SalonFactory.Companion.VoucherHelper.getStatusColor
import com.app.inails.booking.admin.factory.SalonFactory.Companion.VoucherHelper.getStatusName
import com.app.inails.booking.admin.factory.SalonFactory.Companion.VoucherHelper.getTypeCustomer
import com.app.inails.booking.admin.factory.helper.FactoryHelper
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.Schedule
import com.app.inails.booking.admin.model.response.VoucherDTO
import com.app.inails.booking.admin.model.ui.*
import java.text.DecimalFormat

@Inject(ShareScope.Singleton)
class SalonFactory(
    textFormatter: TextFormatter,
    val context: Context,
    val userLocalSource: UserLocalSource
) : FactoryHelper(textFormatter) {

    private fun create(salonDTO: SalonDTO): ISalon {
        return object : ISalon, ISalonDTOOwner {
            override val salonName: String
                get() = salonDTO.name.safe()
            override val slug: String
                get() = salonDTO.slug.safe()
            override val value: SalonDTO get() = salonDTO
            override val address: String
                get() = displaySafe(salonDTO.address)
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
                dayFormat = ISchedule.getDefaultList().find { a -> a.day == it.day }?.dayFormat
                    ?: R.string.monday
            )
        }
    }

    fun createDetail(salonDTO: SalonDTO): ISalonDetail {
        return object : ISalonDetail, ISalon by create(salonDTO) {
            override val salonID: Long
                get() = salonDTO.id
            override val email: String
                get() = textFormatter.displaySafe(salonDTO.email)
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
                get() = salonDTO.vouchers.map { createVoucher(it, context) }.sortedByDescending { it.status }.reversed()
            override val afterGalleryImage: List<AppImage>
                get() = salonDTO.gallery.filter { it.type == 2 }
                    .map { AppImage(id = it.id, path = it.image ?: "") }
            override val beforeGalleryImage: List<AppImage>
                get() = salonDTO.gallery.filter { it.type == 1 }
                    .map { AppImage(id = it.id, path = it.image ?: "") }
        }
    }

    companion object {

        object VoucherHelper{
            fun getTypeCustomer(type: Int?): Int {
                return when (type) {
                    1 -> R.string.customer_type_all
                    2 -> R.string.customer_type_normal
                    else -> R.string.customer_type_vip
                }
            }

            fun getStatusName(status: Int): Int{
                return when (status) {
                    1 -> R.string.happening
                    2 -> R.string.upcoming
                    3 -> R.string.expired
                    else -> R.string.newly_created
                }
            }

            fun getStatusColor(status: Int): Int{
                return when(status){
                    1 -> R.color.colorAccent1
                    2 -> R.color.green
                    3 -> R.color.gray20
                    else -> R.color.red
                }
            }

        }
        fun createVoucher(voucherDto: VoucherDTO, context: Context): IVoucher {
            return object : IVoucher {
                override val code: String
                    get() = voucherDto.code.safe()
                override val typeName: VoucherType
                    get() =
                        when (voucherDto.type) {
                            1 -> VoucherType.PERCENT
                            else -> VoucherType.VALUE
                        }
                override val endDate: String
                    get() = voucherDto.expiration_date.toVoucherTime(
                        context,
                        voucherDto.isAlreadyFormatDate,
                    )
                override val startDate: String
                    get() = voucherDto.start_date.toVoucherTime(
                        context,
                        voucherDto.isAlreadyFormatDate
                    )
                override val typeCustomer: Int
                    get() = getTypeCustomer(voucherDto.type_customer)
                override val valueDiscount: String
                    get() = voucherDto.value.showMaxNumber()
                override val description: String
                    get() = voucherDto.description.safe()
                override val id: Int
                    get() = voucherDto.id
                override val statusName: Int
                    get() = getStatusName(voucherDto.status)
                override val statusColor: Int
                    get() = getStatusColor(voucherDto.status)
                override val status: Int
                    get() = voucherDto.status
            }
        }
    }

}