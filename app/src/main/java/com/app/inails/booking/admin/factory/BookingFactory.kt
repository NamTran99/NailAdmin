package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.AppointmentDTO
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.model.support.ISelector

@Inject(ShareScope.Singleton)
class BookingFactory(private val textFormatter: TextFormatter) {

    private fun createService(serviceDTO: ServiceDTO): IService {
        return object : IService, ISelector by ServiceImpl() {
            override val id: Int
                get() = serviceDTO.id.safe()
            override val name: String
                get() = serviceDTO.name.safe()
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
                get() = textFormatter.formatStatusStaffStatus(staffDTO)
            override val resIconStatus: Int
                get() = textFormatter.formatStatusStaffIcon(staffDTO.status)
            override val colorStatus: Int
                get() = textFormatter.formatStatusStaffColor(staffDTO.status)
            override val timeCheckIn: String
                get() = staffDTO.time_check_in.safe()
        }
    }

    fun createStaffList(staffsDTO: List<StaffDTO>): List<IStaff> {
        return staffsDTO.map(::createStaff).toMutableList()
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
            override val services: String
                get() = appointmentDTO.list_service_names.safe()
            override val staffName: String
                get() = appointmentDTO.staff_name ?: "No request"
            override val statusDisplay: String
                get() = appointmentDTO.status_name.safe()
            override val resIconStatus: Int
                get() = textFormatter.formatStatusAppointmentIcon(appointmentDTO.status)
            override val colorStatus: Int
                get() = textFormatter.formatStatusAppointmentColor(appointmentDTO.status)
        }
    }

    fun createAppointmentList(appointmentsDTO: List<AppointmentDTO>): List<IAppointment> {
        return appointmentsDTO.map(::createAppointment).toMutableList()
    }
}