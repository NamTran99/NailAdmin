package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.viewBinding
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.adapters.CalendarViewBindingAdapter.setDate
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.OpenSalonHourBinding
import com.app.inails.booking.admin.databinding.ViewBusinessHourBinding
import com.app.inails.booking.admin.exception.convert24hTo12hFormat
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.views.dialog.picker.TimePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.TimePickerWithIntervalDialogOwner
import com.app.inails.booking.admin.views.dialog.picker.TimeType

class OpenSalonTime(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet), TimePickerWithIntervalDialogOwner {

    private val binding = viewBinding(OpenSalonHourBinding::inflate)
    private var onTimeChange: ((ISchedule) -> Unit)? = null
    private val data = ISchedule()
    private var mStartHours = 0
    private var mEndHours = 0

    init {
        setupListener()
    }

    @SuppressLint("SetTextI18n")
    fun setupListener() {
        with(binding) {
            tvTotime.onClick{
                timePickerDialog.show(tvTotime.text.toString(), TimeType.End)
            }

            tvFromTime.onClick{
                timePickerDialog.show(tvFromTime.text.toString(), TimeType.Start)
            }

            timePickerDialog.onSubmitClick = {time, hours, type ->
                when (type) {
                  TimeType.Start ->{
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
                    else ->{
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
        }
    }

     fun setStartTime(text: String?) {
        mStartHours = text?.substring(0,2)?.toIntOrNull()?:0
        data.startTime = text
        binding.tvFromTime.text = text?.convert24hTo12hFormat() ?: context.getString(R.string.label_select_time)
         onTimeChange?.invoke(data)
    }

     fun setEndTime(text: String?) {
        mEndHours = text?.substring(0,2)?.toIntOrNull()?:0
        data.endTime = text
        binding.tvTotime.text = text?.convert24hTo12hFormat() ?: context.getString(R.string.label_select_time)
         onTimeChange?.invoke(data)
    }

     fun setOnTimeChange(callBack: (ISchedule) -> Unit) {
        onTimeChange = callBack
    }

}
