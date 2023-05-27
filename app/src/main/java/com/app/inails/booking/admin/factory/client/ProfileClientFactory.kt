package com.app.inails.booking.admin.factory.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.ui.client.IUserClient
import com.app.inails.booking.admin.model.ui.client.IUserClientEdit

@Inject(ShareScope.Singleton)
class ProfileClientFactory(private val textFormatter: TextFormatter) {

    fun displayProfile(it: UserClientDTO?): IUserClient? {
        if (it == null) return null
        return object : IUserClient {
            override val welcomeName: String
                get() = textFormatter.formatWelcomeUser(it.user)
            override val phone: String
                get() = textFormatter.formatPhoneUS(it.user?.phone)
            override val email: String
                get() = textFormatter.noInfo(it.user?.email)
            override val address: String
                get() = textFormatter.noInfo(it.user?.address)
            override val dob: String
                get() = textFormatter.noInfo(it.user?.birthdate)
            override val fullName: String
                get() = it.user?.name.safe()
        }
    }

    fun createEditProfile(userDto: UserClientDTO): IUserClientEdit {
        return object : IUserClientEdit, IUserClient by displayProfile(userDto)!! {
            override val name: String
                get() = userDto.user?.name.safe()
            override val emailEditable: String
                get() = userDto.user?.email.safe()
            override val addressEditable: String
                get() = userDto.user?.address.safe()
            override val dobEditable: String
                get() = userDto.user?.birthdate.safe()
        }
    }

}