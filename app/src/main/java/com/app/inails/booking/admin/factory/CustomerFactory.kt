package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.CustomerDTO
import com.app.inails.booking.admin.model.response.CustomerFullInfoDTO
import com.app.inails.booking.admin.model.ui.ICustomer

@Inject(ShareScope.Singleton)
class CustomerFactory(private val textFormatter: TextFormatter) {
    private fun createCustomer(customerFullInforDTO: CustomerFullInfoDTO):ICustomer {
        val customer = customerFullInforDTO.customer
        return object: ICustomer {
            override val address: String
                get() = customer.address.safe()
            override val email: String
                get() = customer.email.safe()
            override val phone: String
                get() = textFormatter.formatPhoneUS(customer.phone)
            override val id: Int
                get() = customer.id.safe()
            override val name: String
                get() = customer.name.safe()
        }
    }

    fun createCustomerList(customerDTO: List<CustomerFullInfoDTO>): List<ICustomer>{
        return customerDTO.map(::createCustomer)
    }
}