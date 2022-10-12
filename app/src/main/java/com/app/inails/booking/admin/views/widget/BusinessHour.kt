package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Toast
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ViewBusinessHourBinding
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.dialog.picker.TimePickerDialog

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
    FrameLayout(context, attributeSet), BusinessHourViewInf {

    private val mToTimePickerDialog by lazy { TimePickerDialog(context as BaseActivity) }
    private val mFromTimePickerDialog by lazy { TimePickerDialog(context as BaseActivity) }

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
        setupListener()
    }


    @SuppressLint("SetTextI18n")
    private fun setupListener() {
        with(binding) {
            mToTimePickerDialog.apply {
                setupClickWithView(tvTotime)
                onTimePickedListener = { time, hours ->
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
                    }
                }
                onTimeChange?.invoke(data)
            }
            mFromTimePickerDialog.apply {
                setupClickWithView(tvFromTime)
                onTimePickedListener = { time, hours ->
                    if (mEndHours in 1 until hours) {
                        Toast.makeText(
                            context,
                            R.string.message_end_time_greather_than_start_time,
                            Toast.LENGTH_SHORT
                        ).show()
                        setEndTime(null)
                    } else {
                        mStartHours = hours
                        data.startTime = time
                    }

                    onTimeChange?.invoke(data)
                }
            }

            btnReset.onClick {
                tvTotime.text = DEFAULT_TIME
                tvFromTime.text = DEFAULT_TIME
                data.endTime = null
                data.startTime = null
                onTimeChange?.invoke(data)
            }
        }
    }

    override fun setDate(index: Int) {
        date = Day.getDayByIndex(index)
        data.day = index
        binding.tvDate.text = when (date) {
            Day.Monday -> "Monday"
            Day.Tuesday -> "Tuesday"
            Day.Wednesday -> "Wednesday"
            Day.Thursday -> "Thursday"
            Day.Friday -> "Friday"
            Day.Saturday -> "Saturday"
            Day.Sunday -> "Sunday"
        }.apply {
            data.dayFormat = this
        }
    }

    override fun setStartTime(text: String?) {
        mStartHours = text?.substring(0,2)?.toIntOrNull()?:0
        data.startTime = text
        binding.tvFromTime.text = text ?: DEFAULT_TIME
    }

    override fun setEndTime(text: String?) {
        mEndHours = text?.substring(0,2)?.toIntOrNull()?:0
        data.endTime = text
        binding.tvTotime.text = text ?: DEFAULT_TIME
    }

    override fun setOnTimeChange(callBack: (ISchedule) -> Unit) {
        onTimeChange = callBack
    }

    companion object {
        const val DEFAULT_TIME = "Select Time"
    }
}
