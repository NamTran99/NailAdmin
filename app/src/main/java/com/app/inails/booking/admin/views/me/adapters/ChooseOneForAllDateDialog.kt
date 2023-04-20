package com.app.inails.booking.admin.views.me.adapters

import android.app.Activity
import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogChooseOneForAllDateBinding
import com.app.inails.booking.admin.extention.configSpinner
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.bumptech.glide.Glide.init
import com.google.android.youtube.player.internal.s


class ChooseOneForAllDateDialog(context: Context,
) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogChooseOneForAllDateBinding::inflate)
    private lateinit var  selectDateAdapter: SelectDateAdapter

    var onSaveClick : ((List<ISchedule>) -> Unit) = {}

//    var isFirstOpen = true
    private var commonTimeSchedule = ISchedule(startTime = START_TIME, endTime = END_TIME)
    init {
        setCancelable(false)
        selectDateAdapter = SelectDateAdapter(binding.rcDate)
        selectDateAdapter.submit(
            ISchedule.getDefaultList()
        )
        binding.apply {
            businessHour.setOnTimeChange {
                commonTimeSchedule = it
            }
            btCancel.onClick{
                dismiss()
            }
            btSetUp.onClick{
                 if (commonTimeSchedule.startTime == null) {
                     Toast.makeText(context, R.string.error_blank_start_time_1, Toast.LENGTH_SHORT).show()
                     return@onClick
                }

                if (commonTimeSchedule.endTime == null) {
                    Toast.makeText(context, R.string.error_blank_end_time_1, Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                onSaveClick.invoke(
                    selectDateAdapter.selectedItems.let {
                        it.toMutableList().apply { add(currentSelectedDay.day) }
                    }.map {
                        ISchedule(startTime = commonTimeSchedule.startTime, endTime = commonTimeSchedule.endTime, day = it, dayFormat = getDayFormat(it))
                    }
                )

                dismiss()
            }
        }
    }

    var currentSelectedDay = ISchedule()
    fun show(
        currentDate: ISchedule,
        listSchedule: List<ISchedule>
    ) {
        with(binding) {
            val openDay =  listSchedule.filter { it.isOpenDay }
            //config spinner
            val voucherCustomerType = openDay.map { context.getString(it.dayFormat)}.toTypedArray()
            val currentPos = openDay.findIndex { it.day == currentDate.day }
            spDate.configSpinner(false,voucherCustomerType){position ->
                currentSelectedDay = openDay[position].apply { isSelector = true }
                businessHour.setStartTime(openDay[position].startTime)
                businessHour.setEndTime(openDay[position].endTime)
                selectDateAdapter.submit(
                    listSchedule.filter {
                        it.day != currentSelectedDay.day
                    }.map {
                        it.isSelector = (it.startTime == currentSelectedDay.startTime) && (it.endTime == currentSelectedDay.endTime)
                        it
                    }
                )
            }
            spDate.setSelection(currentPos)
            businessHour.setupListener()
        }
        super.show()
    }

    companion object{
        const val START_TIME = "8:00"
        const val END_TIME  = "20:00"
    }

    private fun getDayFormat(day: Int): Int = when(day){
        0 -> R.string.sunday
        1 -> R.string.monday
            2 -> R.string.tuesday
        3 -> R.string.wednesday
        4 -> R.string.thursday
            5 -> R.string.friday
        6 -> R.string.saturday
        else -> R.string.wednesday
    }
}

interface ChooseOneForAllDateDialogOwner : ViewScopeOwner {
    val chooseOneForAllDateDialog: ChooseOneForAllDateDialog
        get() = with(viewScope) {
            getOr("chooseOneForAllDateDialog:dialog") { ChooseOneForAllDateDialog(context) }
        }
}