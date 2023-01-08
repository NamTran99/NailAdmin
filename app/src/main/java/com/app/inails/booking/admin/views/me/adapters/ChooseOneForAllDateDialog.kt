package com.app.inails.booking.admin.views.me.adapters

import android.app.Activity
import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogChooseOneForAllDateBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.bumptech.glide.Glide.init


class ChooseOneForAllDateDialog(context: Context,
) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogChooseOneForAllDateBinding::inflate)
    private lateinit var  selectDateAdapter: SelectDateAdapter

    var onSaveClick : ((List<ISchedule>) -> Unit) = {}

    var isFirstOpen = true
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
                    selectDateAdapter.selectedItems.map {
                        ISchedule(startTime = commonTimeSchedule.startTime, endTime = commonTimeSchedule.endTime, day = it, dayFormat = getDayFormat(it))
                    }
                )

                dismiss()
            }
        }
    }

    fun show(
        function: (String) -> Unit
    ) {
        with(binding) {
            if(isFirstOpen){
                businessHour.setStartTime(START_TIME)
                businessHour.setEndTime(END_TIME)
                isFirstOpen  = false
            }
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