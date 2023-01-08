package com.app.inails.booking.admin.views.dialog.picker

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogTimePickerBinding
import com.app.inails.booking.admin.exception.convert12hTo24hFormat
import com.app.inails.booking.admin.exception.convert24hTo12hFormat
import com.app.inails.booking.admin.extention.onClick
import com.bumptech.glide.Glide.init

class TimePickerWithIntervalDialog(context: Context) :
    BaseDialog(context) {
    private val binding = viewBinding(DialogTimePickerBinding::inflate)

    var onSubmitClick: ((time: String, hour: Int, timeType: TimeType) -> Unit) = { _, _, _ ->}

    init {
        binding.apply {
            btCancel.onClick {
                dismiss()
            }
        }
    }

    fun show(time: String = "20:30 am", timeType: TimeType) {
        binding.apply {
            try {
                timePicker.setInitialSelectedTime(time)
            }catch (e:Exception){
                timePicker.setInitialSelectedTime("12:00 am")
            }
            btSetUp.onClick {
                timePicker.getCurrentlySelectedTime().convert12hTo24hFormat().let{
                    onSubmitClick.invoke(
                        it, it.split(":")[0].toInt(), timeType
                    )
                }
                dismiss()
            }
        }

        super.show()
    }
}

interface TimePickerWithIntervalDialogOwner : ViewScopeOwner {
    val timePickerDialog: TimePickerWithIntervalDialog
        get() = with(viewScope) {
            getOr("timepicker:dialog") { TimePickerWithIntervalDialog(context) }
        }
}

enum class TimeType{
    Start, End
}