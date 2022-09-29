package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogFilterAppointmentBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.extention.toDateAppointment
import com.app.inails.booking.admin.model.ui.AppointmentFilterForm
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog.Companion.FORMAT_DATE_API


class FilterApmDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFilterAppointmentBinding::inflate)

    private val mDatePickerDialog by lazy { DatePickerDialog(context as BaseActivity) }
    private val mToDatePickerDialog by lazy { DatePickerDialog(context as BaseActivity) }

    init {
        with(binding) {
            btClose.onClick {
                dismiss()
            }
            mDatePickerDialog.setDisablePastDates(false)
            mToDatePickerDialog.setDisablePastDates(false)
            mDatePickerDialog.setupClickWithView(tvDateFrom)
            mToDatePickerDialog.setupClickWithView(tvDateTo)
            tvDateFrom.text = ""
            tvDateTo.text = ""
            tvDateTo.tag = ""
            tvDateFrom.tag = ""
        }

    }

    val form = AppointmentFilterForm()

    fun show(
        form: AppointmentFilterForm,
        type: FilterType = FilterType.NORMAL,
        function: (AppointmentFilterForm) -> Unit,
    ) {
        with(binding) {
            (type == FilterType.NORMAL) show lvCustomer
            (type == FilterType.NORMAL) show lvStaff
            tvDateFrom.tag = form.date
            tvDateTo.tag = form.toDate
            tvDateFrom.text = form.date?.toDateAppointment(FORMAT_DATE_API)
            tvDateTo.text = form.toDate?.toDateAppointment(FORMAT_DATE_API)
            etStaff.setText(form.searchStaff)
            etCustomer.setText(form.searchCustomer)
            tvReset.onClick {
                tvDateFrom.text = ""
                tvDateTo.text = ""
                etStaff.setText("")
                etCustomer.setText("")
                form.apply {
                    searchCustomer = null
                    searchStaff = null
                    toDate = null
                    date = null
                }
                function.invoke(form)
                dismiss()
            }

            btSubmit.onClick {
                form.apply {
                    searchCustomer = etCustomer.text.toString()
                    searchStaff = etStaff.text.toString()
                    toDate = tvDateTo.tag?.toString()
                    date = tvDateFrom.tag?.toString()
                }
                function.invoke(form)
                dismiss()
            }

        }
        super.show()
    }
}

enum class FilterType {
    NORMAL,
    FILTER_BY_CUSTOMER
}

interface FilterApmOwner : ViewScopeOwner {
    val filterApmDialog: FilterApmDialog
        get() = with(viewScope) {
            getOr("filter:dialog") {
                FilterApmDialog(context)
            }
        }
}