package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.formatPhoneUSCustom
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.CustomerFullInfoDTO
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.CustomerImpl
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ServiceImpl

@Inject(ShareScope.Singleton)
class CustomerFactory(private val textFormatter: TextFormatter) {
    fun createCustomer(customer: CustomerFullInfoDTO): ICustomer {
        return object : ICustomer by CustomerImpl() {
            override val address: String
                get() = displaySafe(customer.address)
            override val email: String
                get() = displaySafe(customer.email)
            override val phone: String
                get() = customer.phone.safe().formatPhoneUSCustom()
            override val id: Int
                get() = customer.id.safe()
            override val name: String
                get() = displaySafe(customer.name)
            override val birthDay: String
                get() =  displaySafe(customer.birthdate)
            override val type: Int
                get() = customer.type?: 2
            override val note: String
                get() = displaySafe(customer.note)
            override val isShowNote: Boolean
                get() = !customer.note.isNullOrBlank()
        }
    }

    fun createCustomerList(customerDTO: List<CustomerFullInfoDTO>): List<ICustomer> {
        return customerDTO.map(::createCustomer)
    }

    fun createServiceList(servicesDTO: List<ServiceDTO>): List<IService> {
        return servicesDTO.map(::createService)
    }

    private fun createService(serviceDTO: ServiceDTO): IService {
        return object : IService, ISelector by ServiceImpl() {
            override val id: Int
                get() = serviceDTO.id.safe()
            override val name: String
                get() = displaySafe(serviceDTO.name)
            override val price: Double
                get() =  serviceDTO.price.safe()
            override val isActive: Int
                get() = serviceDTO.active.safe()
            override val textColor: Int
                get() = textFormatter.formatTextColorStaffColor(serviceDTO.active.safe())
        }
    }

    fun displaySafe(text: String?): String{
        return textFormatter.displaySafe(text)
    }

//    fun createServiceList(servicesDTO: List<ServiceDTO>): List<IService> {
//        return servicesDTO.map(::createService)
//    }
}