package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ViewBusinessHourBinding
import com.app.inails.booking.admin.exception.convert24hTo12hFormat
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.dialog.picker.TimePickerWithIntervalDialogOwner
import com.app.inails.booking.admin.views.dialog.picker.TimeType

enum class Day(val index: Int) {
    Sunday(0),
    Monday(1),
    Tuesday(2),
    Wednesday(3),
    Thursday(4),
    Friday(5),
    Saturday(6);

    companion object {
        fun getDayByIndex(index: Int): Day {
            Day.values().forEach {
                if (it.index == index) return it
            }
            return Monday
        }
    }
}

interface BusinessHourViewInf {
    fun setDate(index: Int)
    fun setStartTime(text: String?)
    fun setEndTime(text: String?)
    fun setOnTimeChange(callBack: (ISchedule) -> Unit)
}

class BusinessHourView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet), BusinessHourViewInf, TimePickerWithIntervalDialogOwner {

    private val binding = viewBinding(ViewBusinessHourBinding::inflate)
    private var date: Day = Day.Monday
    private var onTimeChange: ((ISchedule) -> Unit)? = null
    private val data = ISchedule()
    private var mStartHours = 0
    private var mEndHours = 0

    init {
        context.loadAttrs(attributeSet, R.styleable.BusinessHourView) {
            setDate(it.getInt(R.styleable.BusinessHourView_date, 0))
        }
        setUpView()
        setupListener()
    }

    @SuppressLint("ResourceAsColor")
    fun setUpView() {
        binding.apply {
            tvStatus.setText(R.string.not_open)
            tvStatus.setTextColor(
                context.getColor(R.color.colorPrimary)
            )
        }
    }

    fun setupListener() {
        with(binding) {
            tvTotime.onClick {
                timePickerDialog.show(tvTotime.text.toString(), TimeType.End)
            }

            tvFromTime.onClick {
                timePickerDialog.show(tvFromTime.text.toString(), TimeType.Start)
            }

            timePickerDialog.onSubmitClick = { time, hours, type ->
                when (type) {
                    TimeType.Start -> {
                        if (mEndHours in 1 until hours) {
                            Toast.makeText(
                                context,
                                R.string.message_end_time_greather_than_start_time,
                                Toast.LENGTH_SHORT
                            ).show()
                            setStartTime(null)
                        } else {
                            mStartHours = hours
                            data.startTime = time
                            tvFromTime.text = time.convert24hTo12hFormat()
                        }

                        onTimeChange?.invoke(data)
                    }
                    else -> {
                        if (hours < mStartHours) {
                            Toast.makeText(
                                context,
                                R.string.message_end_time_greather_than_start_time,
                                Toast.LENGTH_SHORT
                            ).show()
                            setEndTime(null)
                        } else {
                            mEndHours = hours
                            data.endTime = time
                            tvTotime.text = time.convert24hTo12hFormat()
                        }
                        onTimeChange?.invoke(data)
                    }
                }
            }

            btnReset.onClick {
                tvStatus.setText(R.string.not_open)
                tvStatus.setTextColor(
                    context.getColor(R.color.colorPrimary)
                )
                tvTotime.text = context.getString(R.string.label_select_time)
                tvFromTime.text = context.getString(R.string.label_select_time)
                data.endTime = null
                data.startTime = null
                onTimeChange?.invoke(data)
            }
        }
    }

    override fun setDate(index: Int) {
        date = Day.getDayByIndex(index)
        data.day = index
        binding.tvDate.text = context.getString(
            when (date) {
                Day.Monday -> R.string.monday
                Day.Tuesday -> R.string.tuesday
                Day.Wednesday -> R.string.wednesday
                Day.Thursday -> R.string.thursday
                Day.Friday -> R.string.friday
                Day.Saturday -> R.string.saturday
                Day.Sunday -> R.string.sunday
            }.apply {
                data.dayFormat = this
            }
        )
    }

    override fun setStartTime(text: String?) {
        mStartHours = text?.substring(0, 2)?.toIntOrNull() ?: 0
        data.startTime = text
        binding.tvFromTime.text =
            text?.convert24hTo12hFormat() ?: context.getString(R.string.label_select_time)
        if (data.startTime != null && data.endTime != null) {
            binding.tvStatus.setText(R.string.salon_open)
            binding.tvStatus.setTextColor(context.getColor(R.color.colorAccent1))
        }
    }

    override fun setEndTime(text: String?) {
        mEndHours = text?.substring(0, 2)?.toIntOrNull() ?: 0
        data.endTime = text
        binding.tvTotime.text =
            text?.convert24hTo12hFormat() ?: context.getString(R.string.label_select_time)
        if (data.startTime != null && data.endTime != null) {
            binding.tvStatus.setText(R.string.salon_open)
            binding.tvStatus.setTextColor(context.getColor(R.color.colorAccent1))
        }
    }

    override fun setOnTimeChange(callBack: (ISchedule) -> Unit) {
        onTimeChange = callBack
    }


}
