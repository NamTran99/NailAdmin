package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class CheckInOutFactory(private val textFormatter: TextFormatter) {

    private fun createCheckInOutDetail(item: CheckInOutDetailDTO): ICheckInOutDetail {
        return object : ICheckInOutDetail {
            override val dateTimeFormat: String
                get() = item.date_time.toTimeCheckIn("yyyy-MM-dd'T'HH:mm:ss'Z'")
            override val typeInOut: String
                get() = if(item.status == 1) "Check in" else "Check out"
        }
    }

    fun createCheckInOutByDate(item: CheckInOutByDateDTO): ICheckInOutByDate {
        return object : ICheckInOutByDate  {
            override val dateFormat: String
                get() = item.date.toDateCheckIn()
            override val details: List<ICheckInOutDetail>
                get() = createListCheckInOutDetail(item.list_check_in_out)
        }
    }

    private fun createListCheckInOutDetail(items: List<CheckInOutDetailDTO>): List<ICheckInOutDetail> {
        return items.map(::createCheckInOutDetail)
    }

    fun createListCheckInOutByDate(items: List<CheckInOutByDateDTO>): List<ICheckInOutByDate>{
        return items.map(::createCheckInOutByDate)
    }
}
