package com.app.inails.booking.admin.factory

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class CheckInOutFactory(private val textFormatter: TextFormatter,
    val context: Context
) {

    private fun createCheckInOutDetail(item: CheckInOutDetailDTO): ICheckInOutDetail {
        return object : ICheckInOutDetail {
            override val dateTimeFormat: String
                get() = item.date_time.toTimeCheckIn()
            override val typeInOut: String
                get() = context.getString(if(item.status == 1) R.string.btn_check_in else R.string.btn_check_out)
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
