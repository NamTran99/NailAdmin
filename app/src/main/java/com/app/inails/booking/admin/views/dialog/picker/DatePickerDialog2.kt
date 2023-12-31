package com.app.inails.booking.admin.views.dialog.picker

import android.app.DatePickerDialog
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.exception.toDateWithFormat
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialog2(private val activity: BaseActivity) :
    DatePickerDialog(activity, null, 0, 0, 0),
    DatePicker.OnDateChangedListener {
    private var mView: View? = null
    private var mDisableFutureDate = false
    private var mDisablePastDate = true
    private var isDateOfBirth = false
    private var mNumberLimit: Int = 0
    var onDateListener: ((Int, Int, Int) -> Unit)? = null

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

    /**
     * Limit year old
     */
    fun setLimit(number: Int) {
        mNumberLimit = number
    }

    fun enableDateOfBirth() {
        isDateOfBirth = true
        setDisablePastDates(false)
        setDisableFutureDates(true)
    }

    /**
     * Disable future date
     * default: true
     */
    fun setDisableFutureDates(disableFutureDates: Boolean) {
        mDisableFutureDate = disableFutureDates
    }

    fun setDisablePastDates(disablePastDate: Boolean) {
        mDisablePastDate = disablePastDate
    }

    fun setupClickWithView(view: View) {
        mView = view
        view.setOnClickListener {
            this@DatePickerDialog2.handleChooseDate(getViewValue())
        }
        if(isDateOfBirth) return
        val valueCurrent = getViewValue()
        if (valueCurrent.equals(" ", ignoreCase = true)
            || valueCurrent.equals("", ignoreCase = true)
        )
            display(
                SimpleDateFormat(
                    if(isDateOfBirth) FORMAT_DATE_OF_BIRTH else FORMAT_DATE_DISPLAY,
                    Locale.getDefault()
                ).format(Calendar.getInstance().time),
                SimpleDateFormat(
                    FORMAT_DATE_API,
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
            )
    }

    private fun handleChooseDate(timeNow: String) {
        var timeNowL = timeNow
        val calendar = Calendar.getInstance()

        if (timeNowL.equals(" ", ignoreCase = true) || timeNowL.equals("", ignoreCase = true))
            timeNowL = EMPTY_DATE
        val year =
            if (timeNowL == EMPTY_DATE || !timeNowL.contains("-")) calendar.get(Calendar.YEAR) else timeNow.toDateWithFormat(
                formatInput = FORMAT_DATE_API,
                formatOutput = YEAR
            )
        val day =
            if (timeNowL == EMPTY_DATE || !timeNowL.contains("-")) calendar.get(Calendar.DATE) else timeNow.toDateWithFormat(
                formatInput = FORMAT_DATE_API,
                formatOutput = DAY
            )
        val month =
            if (timeNowL == EMPTY_DATE || !timeNowL.contains("-")) calendar.get(Calendar.MONTH) else timeNow.toDateWithFormat(
                formatInput = FORMAT_DATE_API,
                formatOutput = MONTH
            ) - 1

        val datePickerDialog = DatePickerDialog(
            context,
            R.style.AppDatePickerCalendarDialog, { _, year1, monthOfYear, dayOfMonth ->
                calendar.set(year1, monthOfYear, dayOfMonth)
                val simpleDateFormat = SimpleDateFormat(if(isDateOfBirth) FORMAT_DATE_OF_BIRTH else FORMAT_DATE_DISPLAY, Locale.getDefault())
                val simpleDateTagFormat = SimpleDateFormat(FORMAT_DATE_API, Locale.getDefault())
                display(
                    simpleDateFormat.format(calendar.time),
                    simpleDateTagFormat.format(calendar.time)
                )
                onDateListener?.invoke(year1, monthOfYear, dayOfMonth)
            },
            year,
            month,
            day
        )

        applyConfig(datePickerDialog)
        datePickerDialog.show()
        datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        datePickerDialog.getButton(BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        datePickerDialog.getButton(BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.gray))
        datePickerDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun display(date: String, dateTag: String = "") {
        when (mView) {
            is MaterialButton -> {
                (mView as MaterialButton).text = date
                (mView as MaterialButton).tag = dateTag
            }
            is EditText -> {
                (mView as EditText).setText(date)
                (mView as EditText).tag = dateTag
            }
        }
    }

    private fun getViewValue(): String {
        return try {
            when (mView) {
                is MaterialButton -> (mView as MaterialButton).tag.toString()
                is EditText -> (mView as EditText).tag.toString()
                else -> ""
            }
        } catch (ex: NullPointerException) {
            ""
        }
    }

    private fun applyConfig(datePickerDialog: DatePickerDialog) {
        if (mDisableFutureDate)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        if (mDisablePastDate)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        val calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mDay = calendar.get(Calendar.DATE)
        val mMonth = calendar.get(Calendar.MONTH)

        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.DAY_OF_MONTH, mDay)
        maxDate.set(Calendar.MONTH, mMonth)
        maxDate.set(Calendar.YEAR, mYear - mNumberLimit)

        datePicker.maxDate = maxDate.timeInMillis
    }

    companion object {
        private const val MONTH = "MM"
        private const val DAY = "dd"
        private const val YEAR = "yyyy"
        private const val EMPTY_DATE = ""
        const val FORMAT_DATE_API = "yyyy-MM-dd"
        const val FORMAT_DATE_DISPLAY = "MMM dd (EEE)"
        const val FORMAT_DATE_OF_BIRTH = "MM-dd-yyyy"
    }
}