package com.app.inails.booking.admin.views.dialog.picker

import android.app.TimePickerDialog
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.extention.formatTime
import java.util.*

class TimePickerDialog(private val activity: BaseActivity) :
    TimePickerDialog(activity, null, 0, 0, true) {
    private var mOnTimePickedListener: ((hours: String, hoursDisplay: String, minute: String, type: String) -> Unit)? =
        null

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                dismiss()
                super.onDestroy(owner)
            }
        })
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private var mView: View? = null

    fun setupClickWithView(view: View) {
        mView = view
        view.setOnClickListener {
            this@TimePickerDialog.handleChooseTime(getViewValue())
        }
    }

    private fun getViewValue(): String {
        return try {
            when (mView) {
                is TextView -> (mView as TextView).tag.toString()
                else -> ""
            }
        } catch (ex: NullPointerException) {
            ""
        }
    }


    private fun display(time: String) {
        when (mView) {
            is TextView -> {
                (mView as TextView).text = time
            }
        }
    }

    private fun handleChooseTime(timeNow: String) {
        var timeNowL = timeNow
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            activity,
            R.style.AppDatePickerCalendarDialog,
            { _, hourOfDay, minute ->
                var hours = hourOfDay
                var type = "AM"

                if (hourOfDay > 12) {
                    hours = hourOfDay % 12
                    type = "PM"
                }
                if (hourOfDay == 12) {
                    type = "PM"
                }
                val time = "${hourOfDay.formatTime()}:${minute.formatTime()}"
                display(
                    time
                )
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )


        timePickerDialog.show()
        timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        timePickerDialog.getButton(BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        timePickerDialog.getButton(BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.gray))
        timePickerDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }


}
