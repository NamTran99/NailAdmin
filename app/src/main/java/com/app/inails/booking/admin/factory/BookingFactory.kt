package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.AppointmentDTO
import com.app.inails.booking.admin.model.response.CustomerDTO
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class BookingFactory(private val textFormatter: TextFormatter) {

    private fun createService(serviceDTO: ServiceDTO): IService {
        return object : IService, ISelector by ServiceImpl() {
            override val id: Int
                get() = serviceDTO.id.safe()
            override val name: String
                get() = serviceDTO.name.safe()
            override val price: Double
                get() = serviceDTO.price.safe()
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
                get() = staffDTO.time_check_in.safe()
            override val active: Int
                get() = staffDTO.active.safe()
            override val backgroundColor: Int
                get() = textFormatter.formatBackgroundStaffColor(staffDTO.active)
            override val timeCheckInAppointment: String
                get() = staffDTO.appointment_processing?.time_working.safe()
            override val customerName: String
                get() = staffDTO.appointment_processing?.customer_name.safe()
        }
    }

    fun createStaffList(staffsDTO: List<StaffDTO>): List<IStaff> {
        return staffsDTO.map(::createStaff)
    }

    fun createAStaff(staffsDTO: StaffDTO): IStaff {
        return createStaff(staffsDTO)
    }

    private fun createAppointment(appointmentDTO: AppointmentDTO): IAppointment {
        return object : IAppointment {
            override val id: Int
                get() = appointmentDTO.id.safe()
            override val name: String
                get() = appointmentDTO.customer_name.safe()
            override val dateAppointment: String
                get() = appointmentDTO.date_appointment_format
            override val servicesName: String
                get() = appointmentDTO.list_service_names.safe()
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
                get() = appointmentDTO.customer.phone.safe()
            override val customer: ICustomer
                get() = createCustomer(appointmentDTO.customer)
            override val notes: String?
                get() = appointmentDTO.notes

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
                get() = customerDTO.name.safe()
            override val phone: String
                get() = textFormatter.formatPhoneUS(customerDTO.phone)
            override val email: String
                get() = customerDTO.email.safe("No Info")
            override val address: String
                get() = textFormatter.fullAddress(customerDTO)
        }
    }
}