package com.app.inails.booking.admin.factory.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_ACCEPTED
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_CANCEL
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_FINISH
import com.app.inails.booking.admin.DataConst.StaffStatus.STAFF_AVAILABLE
import com.app.inails.booking.admin.DataConst.VoucherType.TYPE_PERCENT
import com.app.inails.booking.admin.exception.toDateLocal
import com.app.inails.booking.admin.extention.displaySafe1
import com.app.inails.booking.admin.extention.isCurrentDate
import com.app.inails.booking.admin.extention.noInfo
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.firebase.NotificationBookingDTO
import com.app.inails.booking.admin.model.response.client.BookingClientDTO
import com.app.inails.booking.admin.model.response.client.ServiceClientDTO
import com.app.inails.booking.admin.model.response.client.StaffClientDTO
import com.app.inails.booking.admin.model.response.client.VoucherClientDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.model.ui.client.*

@Inject(ShareScope.Singleton)
class BookingClientFactory(private val textFormatter: TextFormatter) {

    private fun createService(serviceDTO: ServiceClientDTO?): IServiceClient {
        return object : IServiceClient, ISelector by ServiceImpl() {
            override val id: Int
                get() = serviceDTO?.id.safe()
            override val name: String
                get() = serviceDTO?.name.safe()
            override val price: String
                get() = textFormatter.formatSalonPrice(serviceDTO)
            override val images: List<String>?
                get() = textFormatter.updateImageForService(serviceDTO!!)
            override val priceF: Float
                get() = serviceDTO?.price.safe()
        }
    }

    private fun createStaff(staffDTO: StaffClientDTO, dateTime: String): IStaffClient {
        return object : IStaffClient, ISelector by IStaffImpl() {
            override val id: Int
                get() = staffDTO.id.safe()
            override val name: String
                get() = textFormatter.fullName(staffDTO)
            override val status: Pair<Int, Int>
                get() = textFormatter.statusStaff(staffDTO.status)
            override val isShowStatus: Boolean
                get() = dateTime.isCurrentDate()
            override val avatar: String
                get() = staffDTO.avatar.safe()
        }
    }

    fun createTimeList(): List<ITime> {
        return DataConst.Mock.times
    }

    fun createServiceList(servicesDTO: List<ServiceClientDTO>): List<IServiceClient> {
        return servicesDTO.map(::createService)
    }

    fun createStaffs(
        staffs: List<StaffClientDTO>,
        isFilterAvailable: Boolean,
        dateTime: String
    ): List<IStaffClient> {
        var staffF = staffs
        if (isFilterAvailable) {
            staffF = staffs.filter { it.status == STAFF_AVAILABLE }
        }
        return staffF.map { createStaff(it, dateTime) }
    }

    fun createBooking(booking: BookingClientDTO?): IBooking {
        return object : IBooking {
            override val bookingIdDisplay: String
                get() = textFormatter.formatBookingID(booking?.id)
            override val bookingID: Long
                get() = booking?.id.safe()
            override val slug: String
                get() = booking?.salon?.slug.safe()
            override val salonID: Long
                get() = booking?.salonId.safe()
            override val salonName: String
                get() = booking?.salon?.name.safe()
            override val customerName: String
                get() = booking?.customerName.safe()
            override val staffName: String
                get() = booking?.staffName.safe()
            override val dateTime: String
                get() = booking?.dateTimestamp?.toDateLocal().safe()
            override val customerPhone: String
                get() = textFormatter.formatPhoneUS(booking?.customer?.phone)
            override val services: List<IServiceClient>?
                get() = booking?.services?.map(::createService)
            override val hasVoucher: Boolean
                get() = booking?.voucher != null
            override val totalAmount: String
                get() = textFormatter.formatPrice(booking?.price).safe()
            override val discount: String
                get() = "-${textFormatter.formatPrice(booking?.totalDiscount)}"
            override val percent: String
                get() = booking?.voucher?.value?.let { textFormatter.formatPercent(it) }.safe()
            override val totalPrice: String
                get() = textFormatter.formatPrice(booking?.totalPrice)
            override val showPercent: Boolean
                get() = booking?.voucher?.type == TYPE_PERCENT
            override val voucherInfo: String
                get() =  "${booking?.voucher?.description}\nStart date: ${booking?.voucher?.startDate}\nExpiration date: ${booking?.voucher?.expirationDate}"
            override val voucherCode: String
                get() = booking?.voucher?.code.displaySafe1()
        }
    }

    fun createAppointments(it: List<BookingClientDTO>): List<IAppointmentClient> {
        return it.map(::createAppointment)
    }

    fun createAppointment(bookingDTO: BookingClientDTO): IAppointmentClient {
        return object : IAppointmentClient, StatusEditable, IBooking by createBooking(bookingDTO) {
            override val id: Long
                get() = bookingDTO.id.safe()
            override val salonID: Long
                get() = bookingDTO.salonId.safe()
            override val idDisplay: String
                get() = textFormatter.displayIDAppointment(bookingDTO.id)
            override val serviceDisplay: String
                get() = bookingDTO.list_service_names_with_price.safe()
            override var status: Triple<Int, Int, Int> =
                textFormatter.statusAppointment(bookingDTO.status)
            override var isShowCancelButton: Boolean = textFormatter.isCancelAppointment(bookingDTO)
            override var canceledBy: String = textFormatter.canceledBy(bookingDTO.canceledByName)
            override var reasonCancel: String = bookingDTO.reason_cancel.safe()
            override val isFinish: Boolean
                get() = bookingDTO.status ==APM_FINISH
            override val isShowDirect: Boolean
                get() = bookingDTO.status == APM_ACCEPTED
            override val totalAmount: String
                get() = textFormatter.formatPrice(bookingDTO.price).safe()
            override val finishNote: String
                get() = bookingDTO.notes.safe()
            override val hasFeedBack: Boolean
                get() = bookingDTO.feedback != null
            override val feedbackContent: String
                get() = bookingDTO.feedback?.content.safe()
            override val feedbackRating: Float
                get() = bookingDTO.feedback?.rating.safe()
            override val isShowNote: Boolean
                get() = isFinish && finishNote.isNotEmpty()
            override val isShowCancelNote: Boolean
                get() = canceledBy.isNotEmpty()
            override val lat: Float
                get() = bookingDTO.salon?.lat.safe()
            override val lng: Float
                get() = bookingDTO.salon?.lng.safe()
            override val createAtDisplay: String
                get() = bookingDTO.createAtTimestamp?.toDateLocal().safe()
            override val note: String
                get() = bookingDTO.note.noInfo()

            override fun setCancelStatus(reason: String) {
                status = textFormatter.statusAppointment(APM_CANCEL)
                isShowCancelButton = false
                this.canceledBy = textFormatter.canceledBy(isGuest = true)
                reasonCancel = reason
            }

            override val feedbackImages: List<String>?
                get() = bookingDTO.feedback?.images?.map { it.image }

            override val imagesBefore: List<String>?
                get() = bookingDTO.images?.filter { it.typeName == "before" }?.map { it.image }
            override val imagesAfter: List<String>?
                get() = bookingDTO.images?.filter { it.typeName == "after" }?.map { it.image }

            override val hasVoucher: Boolean
                get() = bookingDTO.voucher != null

            override val discount: String
                get() = "-${textFormatter.formatPrice(bookingDTO.totalDiscount)}"
            override val percent: String
                get() = bookingDTO.voucher?.value?.let { textFormatter.formatPercent(it) }.safe()
            override val totalPrice: String
                get() = textFormatter.formatPrice(bookingDTO.totalPrice)
            override val showPercent: Boolean
                get() = bookingDTO.voucher?.type == TYPE_PERCENT
            override val voucherInfo: String
                get() =  "${bookingDTO.voucher?.description?:""}\nStart date: ${bookingDTO.voucher?.startDate?.toDateLocal("yyyy-MM-dd'T'HH:mm:ss'Z'")}\nExpiration date: ${bookingDTO.voucher?.expirationDate?.toDateLocal("yyyy-MM-dd'T'HH:mm:ss'Z'")}"
        }
    }

    fun createNotificationBooking(
        notificationID: String?,
        it: NotificationBookingDTO?
    ): IBookingNotification {
        return object : IBookingNotification {
            override val id: Long
                get() = notificationID?.toLongOrNull().safe()
            override val bookingIdDisplay: String
                get() = textFormatter.displayIDAppointment(it?.id)
            override val bookingID: Long
                get() = it?.id.safe()
            override val slug: String
                get() = it?.salonSlug.safe()
            override val lat: Float
                get() = it?.lat.safe()
            override val lng: Float
                get() = it?.lng.safe()
            override val salonName: String
                get() = it?.salonName.safe()
            override val staffName: String
                get() = it?.staffName.safe()
            override val dateTime: String
                get() = it?.datetime?.toDateLocal().safe()
            override val reason: String
                get() = it?.reasonCancel.safe()
            override val services: List<IServiceClient>?
                get() = it?.services?.map(::createService)
            override val totalPrice: String
                get() = textFormatter.formatPrice(it?.price)
            override val notes: String
                get() = it?.notes.safe()
            override val customerName: String
                get() = it?.customerName.safe()
            override val customerPhone: String
                get() = textFormatter.formatPhoneUS(it?.customerPhone)
        }
    }

    fun createVoucher(total: Float, voucherDTO: VoucherClientDTO): IVoucherClient {
        return object : IVoucherClient {
            override val id: Long
                get() = voucherDTO.id
            override val title: String
                get() = voucherDTO.title
            override val type: Int
                get() = voucherDTO.type
            override val code: String
                get() = voucherDTO.code
            override val discount: String
                get() =  "-${textFormatter.formatPriceDiscount(total, voucherDTO)}"
            override val percent: String
                get() = textFormatter.formatPercent(voucherDTO.value)
            override val totalAmount: String
                get() = textFormatter.formatTotalAmount(total, voucherDTO)
            override val description: String
                get() = voucherDTO.description.safe()
            override val startDate: String
                get() =  voucherDTO.startDate.toDateLocal()
            override val expirationDate: String
                get() = voucherDTO.expirationDate.toDateLocal()

        }
    }
}