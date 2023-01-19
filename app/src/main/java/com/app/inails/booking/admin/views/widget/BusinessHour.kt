package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Toast
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ViewBusinessHourBinding
import com.app.inails.booking.admin.exception.convert24hTo12hFormat
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
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
    fun setTime(startTime: String?, endTime: String?)
    fun setOnTimeChange(callBack: (ISchedule) -> Unit)
}

class BusinessHourView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet), BusinessHourViewInf, TimePickerWithIntervalDialogOwner {

    private val binding = viewBinding(ViewBusinessHourBinding::inflate)
    private var date: Day = Day.Monday
    private var onTimeChange: ((ISchedule) -> Unit)? = null
    private val data = ISchedule()

    var mStartTime : String? = null //format 24
        get() = data.startTime
        set(value){
            mStartHours = value?.substring(0, 2)?.toIntOrNull() ?: 0
            data.startTime = value
            binding.tvFromTime.text =
                value?.convert24hTo12hFormat() ?: context.getString(R.string.label_select_time)
            checkShowReset()
            field = value
        }

    var mEndTime : String? = null //format 24
        get() = data.endTime
        set(value){
            mEndHours = value?.substring(0, 2)?.toIntOrNull() ?: 0
            data.endTime = value
            binding.tvTotime.text =
                value?.convert24hTo12hFormat() ?: context.getString(R.string.label_select_time)
            checkShowReset()
            field = value
        }

    var isShowReset: Boolean = true
        set(value) {
            field =value
            binding.btnReset.show(value)
        }

    var isOpen: Boolean = false
        set(value) {
            binding.apply {
                if(value){
                    tvTotime.isEnabled = true
                    tvFromTime.isEnabled = true
                    btnReset.isEnabled = true
                    tvStatus.setText(R.string.not_open)
                    lvMain.alpha = 1f
                    tvStatus.setTextColor(context.getColor(R.color.colorAccent1))
                }else{
                    tvTotime.isEnabled = false
                    tvFromTime.isEnabled = false
                    btnReset.isEnabled = false
                    lvMain.alpha = 0.3f
                    tvStatus.setText(R.string.salon_open)
                    tvStatus.setTextColor(
                        context.getColor(R.color.colorPrimary)
                    )
                }
                data.isOpenDay = value
                field  = value
            }
        }

    private var mStartHours = 0
    private var mEndHours = 0

    init {
        context.loadAttrs(attributeSet, R.styleable.BusinessHourView) {
            setDate(it.getInt(R.styleable.BusinessHourView_date, 0))
        }
        setUpView()
        setupListener()
    }

    fun checkShowReset(){
        binding.btnReset.show(mStartTime != null || mEndTime != null)
    }

    @SuppressLint("ResourceAsColor")
    fun setUpView() {
        binding.apply {
            isOpen = true
        }
    }

    fun setupListener() {
        with(binding) {
            tvStatus.onClick{
                isOpen = !isOpen
            }
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
                            mStartTime = null
                        } else {
                            mStartHours = hours
                            mStartTime = time
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
                            mEndTime = null
                        } else {
                            mEndHours = hours
                            mEndTime = time
                        }
                        onTimeChange?.invoke(data)
                    }
                }
            }

            btnReset.onClick {
                tvTotime.text = context.getString(R.string.label_select_time)
                tvFromTime.text = context.getString(R.string.label_select_time)
                btnReset.hide()
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


    override fun setTime(startTime: String?, endTime:String?){
        mStartTime = startTime
        mEndTime = endTime

        binding.btnReset.show(data.startTime != null || data.endTime != null)
        isOpen = !(data.startTime == null && data.endTime == null)
        onTimeChange?.invoke(data)
    }

    override fun setOnTimeChange(callBack: (ISchedule) -> Unit) {
        onTimeChange = callBack
    }
}

