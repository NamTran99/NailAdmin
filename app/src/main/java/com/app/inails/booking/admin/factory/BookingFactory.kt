package com.app.inails.booking.admin.factory

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.DataConst.StaffStatus.STAFF_AVAILABLE
import com.app.inails.booking.admin.DataConst.StaffStatus.STAFF_BREAK
import com.app.inails.booking.admin.DataConst.StaffStatus.STAFF_WORKING
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.SalonFactory.Companion.createVoucher
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class BookingFactory(
    private val textFormatter: TextFormatter,
    val context: Context
) {

    fun displaySafe(text: String?): String{
        return textFormatter.displaySafe(text)
    }

    private fun createService(serviceDTO: ServiceDTO): IService {
        return object : IService, ISelector by ServiceImpl() {
            override val id: Int
                get() = serviceDTO.id.safe()
            override val name: String
                get() = serviceDTO.name.safe()
            override val price: Double
                get() = serviceDTO.price.safe()
            override val isActive: Int
                get() = serviceDTO.active.safe()
            override val textColor: Int
                get() = textFormatter.formatTextColorStaffColor(serviceDTO.active.safe())
            override val detailImages: List<AppImage>
                get() = serviceDTO.images.map { AppImage(it.id, it.image) }.toMutableList()
                    .apply { if (serviceDTO.image != null) add(AppImage(image = serviceDTO.image)) }
            override val avatar: String?
                get() = serviceDTO.image
            override val moreImage: List<AppImage>
                get() = serviceDTO.images.map { AppImage(it.id, it.image) }
        }
    }

    fun createServiceList(servicesDTO: List<ServiceDTO>): List<IService> {
        return servicesDTO.map(::createService)
    }

    fun createAService(serviceDTO: ServiceDTO): IService {
        return createService(serviceDTO)
    }

    private fun createStaff(staffDTO: StaffDTO): IStaff {
        return object : IStaff, ISelector by StaffImpl() {
            override val id: Int
                get() = staffDTO.id.safe()
            override val name: String
                get() = textFormatter.fullName(staffDTO)
            override val firstName: String
                get() = staffDTO.first_name.safe()
            override val lastName: String
                get() = staffDTO.last_name.safe()
            override val phone: String
                get() = textFormatter.formatPhoneUS(staffDTO.phone)
            override val status: Int
                get() = staffDTO.status.safe()
            override val statusName: String
                get() = staffDTO.status_name.safe()
            override val resIconStatus: Int
                get() = textFormatter.formatStatusStaffIcon(staffDTO.status)
            override val colorStatus: Int
                get() = textFormatter.formatStatusStaffColor(staffDTO.status)
            override val timeCheckIn: String
                get() = staffDTO.checked_time?.toTimeCheckIn().safe()
            override val active: Int
                get() = staffDTO.active.safe()
            override val textColor: Int
                get() = textFormatter.formatTextColorStaffColor(staffDTO.active)
            override val timeCheckInAppointment: String
                get() = staffDTO.appointment_processing?.date_appointment.toTimeCheckIn(format = "yyyy-MM-dd'T'HH:mm:ss")
                    .safe()
            override val customerName: String
                get() = staffDTO.appointment_processing?.customer_name?: ""
            override val appointment: IAppointment?
                get() = staffDTO.appointment_processing?.let { createAAppointment(it) }
            override val timeEndAppointment: String
                get() = textFormatter.getTimeEndAppointment(
                    staffDTO.appointment_processing?.date_appointment,
                    staffDTO.appointment_processing?.work_time,
                )
            override val orderStaffStatusList: Int
                get() = getOrderPriorityForStaffStatus(staffDTO.status.safe())
            override val avatar: String?
                get() = staffDTO.avatar
            override val description: String
                get() = staffDTO.description ?: ""
            override val isAvatarDefault: Boolean
                get() = staffDTO.is_avatar_default ?: false
        }
    }

    fun createStaffList(staffsDTO: List<StaffDTO>): List<IStaff> {
        return staffsDTO.map(::createStaff)
    }

    fun createAStaff(staffsDTO: StaffDTO): IStaff {
        return createStaff(staffsDTO)
    }

    fun createReportAppointment(reportDTO: ReportDTO): IReport {
        return object : IReport {
            override val appointment: List<IAppointment>
                get() = createAppointmentList(reportDTO.appointments)
            override val totalAppointment: Int
                get() = reportDTO.total_appointment
            override val totalPrice: String
                get() = reportDTO.total_price
        }
    }

    private fun createAppointment(appointmentDTO: AppointmentDTO): IAppointment {
        return object : IAppointment {
            override val serviceListAll: List<IService>
                get() = appointmentDTO.services.toMutableList().apply {
                    addAll(appointmentDTO.service_custom)
                }.map(::createService)
            override val somethingElse: String
                get() = appointmentDTO.something_else
            override val id: Int
                get() = appointmentDTO.id.safe()
            override val customerName: String
                get() = appointmentDTO.customer_name.safe()
            override val dateAppointment: String
                get() = textFormatter.getDateTimeWithEndAppointment(
                    appointmentDTO.date_appointment!!,
                    appointmentDTO.work_time
                )
            override val servicesName: String
                get() = appointmentDTO.list_service_names_with_price.safe()
            override val staffName: String
                get() = appointmentDTO.staff_name ?: "No request"
            override val statusDisplay: String
                get() = appointmentDTO.status_name.safe()
            override val resIconStatus: Int
                get() = textFormatter.formatStatusAppointmentIcon(
                    appointmentDTO.status,
                    appointmentDTO.type
                )
            override val colorStatus: Int
                get() = textFormatter.formatStatusAppointmentColor(
                    appointmentDTO.status,
                    appointmentDTO.type
                )
            override val status: Int
                get() = appointmentDTO.status
            override val staffID: Int
                get() = appointmentDTO.staff_id.safe()
            override val type: Int
                get() = appointmentDTO.type
            override val canceledBy: String
                get() = textFormatter.formatStatusAppointmentCancel(appointmentDTO.canceled_by_name)
            override val serviceList: List<IService>
                get() = appointmentDTO.services.map(::createService)
            override val totalPrice: Double
                get() = appointmentDTO.total_price_service.safe()
            override val phone: String
                get() = appointmentDTO.customer.phone_format.safe()
            override val customer: ICustomer
                get() = createCustomer(appointmentDTO.customer)
            override val staff: IStaff?
                get() = appointmentDTO.staff?.let { createStaff(it) }
            override val notes: String
                get() = appointmentDTO.note_appointment.safe()
            override val dateTime: String
                get() = appointmentDTO.date_appointment.safe()
            override val reasonCancel: String
                get() = appointmentDTO.reason_cancel.safe()
            override val hasFeedback: Boolean
                get() = appointmentDTO.feedback != null
            override val feedbackContent: String
                get() = appointmentDTO.feedback?.content.safe().decode()
            override val feedbackRating: Float
                get() = appointmentDTO.feedback?.rating.safe()
            override val noteFinish: String
                get() = appointmentDTO.notes.safe()
            override val totalPriceService: String
                get() = appointmentDTO.total_price_service_format.toPriceFormat()
            override val workTime: Int
                get() = appointmentDTO.work_time.safe()
            override val serviceCustom: List<IService>
                get() = appointmentDTO.service_custom.map { createService(it) }
            override val createAt: String
                get() = appointmentDTO.created_at_timestamp.convertFromServerToLocalTime()
            override val dateSelected: String
                get() = appointmentDTO.date_appointment!!.toDateAppointment(formatTz = "UTC")
            override val dateTag: String
                get() = appointmentDTO.date_appointment!!.toDateTagAppointment()
            override val timeSelected: String
                get() = appointmentDTO.date_appointment!!.toTimeAppointment()
            override val customerID: Int
                get() = appointmentDTO.customer_id.safe()
            override val feedbackImages: List<AppImage>
                get() = appointmentDTO.feedback?.images?.map { AppImage(image = it.image) }
                    ?: listOf()
            override val afterImage: List<AppImage>
                get() = appointmentDTO.images.filter { it.type_name == "after" }
                    .map { AppImage(image = it.image ?: "") }
            override val beforeImage: List<AppImage>
                get() = appointmentDTO.images.filter { it.type_name == "before" }
                    .map { AppImage(image = it.image ?: "") }
            override val totalAmountDisplay: String
                get() = textFormatter.formatPrice(appointmentDTO.price).safe()
            override val totalAmount: Double
                get() = appointmentDTO.price.safe()
            override val discount: String
                get() = "-${appointmentDTO.total_discount_format.toPriceFormat()}"
            override val hasVoucher: Boolean
                get() = appointmentDTO.voucher != null
            override val percentDisplay: String
                get() = appointmentDTO.voucher?.value?.let { textFormatter.formatPercent(it) }
                    .safe()
            override val percent: Double
                get() = appointmentDTO.voucher?.value.safe()
            override val showPercent: Boolean
                get() = appointmentDTO.voucher?.type == DataConst.VoucherType.TYPE_PERCENT
            override val voucherCode: String
                get() = displaySafe(appointmentDTO.voucher?.code)
            override val voucher: IVoucher?
                get() = appointmentDTO.voucher?.let { createVoucher(it, context) }
        }
    }

    fun createAppointmentList(appointmentsDTO: List<AppointmentDTO>): List<IAppointment> {
        return appointmentsDTO.map(::createAppointment)
    }

    fun createAAppointment(appointmentDTO: AppointmentDTO): IAppointment {
        return createAppointment(appointmentDTO)
    }

    private fun createCustomer(customerDTO: CustomerDTO): ICustomer {
        return object : ICustomer {
            override val id: Int
                get() = customerDTO.id.safe()
            override val name: String
                get() = displaySafe(customerDTO.name)
            override val phone: String
                get() = displaySafe(customerDTO.phone_format)
            override val email: String
                get() = displaySafe(customerDTO.email)
            override val address: String
                get() = displaySafe(textFormatter.fullAddress(customerDTO))
            override val birthDay: String
                get() = displaySafe(customerDTO.birthdate)
        }
    }

    private fun getOrderPriorityForStaffStatus(status: Int): Int {
        return when (status) {
            STAFF_AVAILABLE -> 1
            STAFF_WORKING -> 2
            STAFF_BREAK -> 3
            else -> 4
        }
    }
}