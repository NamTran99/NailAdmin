package com.app.inails.booking.admin.views.dialog.picker

import android.app.TimePickerDialog
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import java.util.*

class TimePickerDialog2(private val activity: BaseActivity) :
    TimePickerDialog(activity, null, 0, 0, true) {
    private var mOnTimePickedListener: ((hours: String, hoursDisplay: String, minute: String, type: String) -> Unit)? =
        null
    private var onChangeTimeListener: (() -> Unit)? = null

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

    fun setupClickWithView(view: View, onChangeListener: () -> Unit) {
        onChangeTimeListener = onChangeListener
        mView = view
        view.setOnClickListener {
            this@TimePickerDialog2.handleChooseTime(getViewValue())
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
            ContextThemeWrapper(activity, R.style.AppDatePickerCalendarDialog),
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
                val time = "${formatTime(hourOfDay)}:${formatTime(minute)}"
                display(
                    time
                )
                onChangeTimeListener?.invoke()
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

    private fun formatTime(time: Int): String {
        return if (time < 10) "0$time"
        else "$time"
    }

}
