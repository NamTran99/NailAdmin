package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.response.TimeZoneDTO
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ITimeZone
import com.app.inails.booking.admin.model.ui.ServiceImpl

@Inject(ShareScope.Singleton)
class TimeZoneFactory {

    fun createTimeZone(timeZoneDTO: TimeZoneDTO): ITimeZone {
        return object : ITimeZone {
            override val dstOffset: Int
                get() = timeZoneDTO.dstOffset
            override val rawOffset: Int
                get() = timeZoneDTO.rawOffset
            override val status: String
                get() = timeZoneDTO.status
            override val timeZoneId: String
                get() = timeZoneDTO.timeZoneId
            override val timeZoneName: String
                get() = timeZoneDTO.timeZoneName
        }
    }
}