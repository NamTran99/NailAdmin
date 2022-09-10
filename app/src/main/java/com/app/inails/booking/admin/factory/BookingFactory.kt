package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.model.ui.StaffImpl
import com.app.inails.booking.formatter.TextFormatter
import com.app.inails.booking.model.support.ISelector

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

    private fun createStaff(staffDTO: StaffDTO): IStaff {
        return object : IStaff, ISelector by StaffImpl() {
            override val id: Int
                get() = staffDTO.id.safe()
            override val name: String
                get() = textFormatter.fullName(staffDTO)
            override val phone: String
                get() = textFormatter.formatPhoneUS(staffDTO.phone)

        }
    }

    fun createStaffList(staffsDTO: List<StaffDTO>): List<IStaff> {
        return staffsDTO.map(::createStaff)
    }
}