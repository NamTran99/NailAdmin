package com.app.inails.booking.admin.views.booking.dialog_filter

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_ACCEPTED
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_CANCEL
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_PENDING
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogFilterAppointmentBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.AppointmentFilterForm
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog.Companion.FORMAT_DATE_API
import java.util.*


class FilterApmDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFilterAppointmentBinding::inflate)
    private val mFromDatePickerDialog by lazy { DatePickerDialog(context as BaseActivity) }
    private val mToDatePickerDialog by lazy { DatePickerDialog(context as BaseActivity) }

    private var mFromDate: Calendar? = null
    private var mToDate: Calendar? = null

    init {
        with(binding) {
            btClose.onClick {
                dismiss()
            }
            mFromDatePickerDialog.apply {
                setDisablePastDates(false)
                setupClickWithView(tvDateFrom)
                onDateListener = {
                    mFromDate = it
                }
            }

            mToDatePickerDialog.apply {
                setDisablePastDates(false)
                setupClickWithView(tvDateTo)
                onDateListener = {
                    mToDate = it
                }
            }

            tvDateFrom.text = ""
            tvDateTo.text = ""
            tvDateTo.tag = ""
            tvDateFrom.tag = ""
        }
    }

    var mStatus: Int? = null
    var mStaff: IStaff? = null
    var mCustomer: ICustomer? = null
    fun show(
        form: AppointmentFilterForm,
        type: FilterType = FilterType.NORMAL,
        openSearchStaff: (() -> Unit)? = null,
        openSearchCustomer: (() -> Unit)? = null,
        onSummitData: (AppointmentFilterForm) -> Unit,
    ) {

        with(binding) {

            setUpViewByType(type)

            tvDateFrom.tag = form.fromDate
            tvDateTo.tag = form.toDate
            tvDateFrom.text = form.fromDate?.toDateAppointment(FORMAT_DATE_API)
            tvDateTo.text = form.toDate?.toDateAppointment(FORMAT_DATE_API)
            if (form.staff != null) {
                updateStaff(form.staff!!)
            } else
                tvStaff.text = ""
            if (form.customer != null) {
                updateCustomer(form.customer!!)
            } else
                tvCustomer.text = ""
            mStatus = form.status
            mCustomer = form.customer
            mStaff = form.staff
            displayStatusList(form.type)
            tvReset.onClick {
                dismiss()
                tvDateError.hide()
                mFromDate = null
                mToDate = null
                tvDateFrom.text = ""
                tvDateTo.text = ""
                tvStaff.text = ""
                tvCustomer.text = ""
                mStaff = null
                mStatus = null
                mCustomer = null
                form.apply {
                    staff = null
                    customer = null
                    toDate = null
                    fromDate = null
                    status = if(type == FilterType.FILTER_IN_REPORT) status else null
                    searchStaff = if(type == FilterType.FILTER_BY_STAFF) searchStaff else null
                    searchCustomer = if(type == FilterType.FILTER_BY_CUSTOMER) searchCustomer else null
                }
                onSummitData.invoke(form)
            }

            btSubmit.onClick {
                form.apply {
                    customer = mCustomer
                    staff = mStaff
                    toDate = tvDateTo.tag?.toString()
                    fromDate = tvDateFrom.tag?.toString()
                    status = mStatus
                }

                if(isValidRangeDate()){
                    onSummitData.invoke(form)
                    dismiss()
                }
            }

            tvCustomer.setOnClickListener {
                openSearchCustomer?.invoke()
            }
            tvStaff.setOnClickListener {
                openSearchStaff?.invoke()
            }

        }
        super.show()
    }

    private fun setUpViewByType(type: FilterType) {
        with(binding){
            lvCustomer.show(type != FilterType.FILTER_BY_CUSTOMER)
            lvStaff.show(type != FilterType.FILTER_BY_STAFF)
            lvStatus.show(type != FilterType.FILTER_IN_REPORT)
        }
    }

    val statusViews = linkedMapOf<Int, CheckBox>()

    private fun displayStatusList(type: Int?) {
        statusViews.clear()
        val statusList = linkedMapOf<Int, Int>()
        when (type) {
            2 -> {
                statusList[APM_PENDING] = R.string.label_status_waiting_for_approval
                statusList[APM_ACCEPTED] = R.string.label_accepted
                statusList[APM_CANCEL] = R.string.label_canceled
            }
            1 -> {
                statusList[DataConst.AppointmentStatus.APM_WAITING] =
                    R.string.label_status_waiting_for_service
                statusList[DataConst.AppointmentStatus.APM_IN_PROCESSING] =
                    R.string.label_status_serving
                statusList[DataConst.AppointmentStatus.APM_FINISH] = R.string.label_status_finished
            }
            else -> {
                statusList[APM_PENDING] = R.string.label_status_waiting_for_approval
                statusList[APM_ACCEPTED] = R.string.label_accepted
                statusList[APM_CANCEL] = R.string.label_canceled
                statusList[DataConst.AppointmentStatus.APM_WAITING] =
                    R.string.label_status_waiting_for_service
                statusList[DataConst.AppointmentStatus.APM_IN_PROCESSING] =
                    R.string.label_status_serving
                statusList[DataConst.AppointmentStatus.APM_FINISH] = R.string.label_status_finished
            }
        }

        binding.statusLayout.removeAllViews()
        val margin10 = context.resources.getDimension(R.dimen.size_10).toInt()
        statusList.map {
            val cbStatusName = CheckBox(context)
            cbStatusName.setText(it.value)
            cbStatusName.tag = it.key
            cbStatusName.isChecked = it.key == mStatus
            cbStatusName.typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
            cbStatusName.buttonDrawable = null
            cbStatusName.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.text_size_12)
            )
            cbStatusName.setPadding(context.resources.getDimensionPixelSize(R.dimen.size_8))
            cbStatusName.setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.color_status_filter
                )
            )
            cbStatusName.setBackgroundResource(R.drawable.custom_status_select)
            cbStatusName.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    if (isChecked) {
                        mStatus = buttonView.tag as Int
                        statusViews.map {
                            if (it.key != buttonView.tag) {
                                statusViews[it.key]?.isChecked = false
                            }
                        }

                    } else {
                        mStatus = null
                    }
                }

            }
            statusViews[it.key] = cbStatusName
            binding.statusLayout.addView(
                cbStatusName,
                ViewGroup.LayoutParams(margin10, margin10)
            )
        }
    }

    fun updateCustomer(it: ICustomer?) {
        if (it != null){
            mCustomer = it
            binding.tvCustomer.text = "${it.name} - ${it.phone.formatPhoneUSCustom()}"
        }
    }

    fun updateStaff(it: IStaff?) {
        if (it != null){
            mStaff = it
            binding.tvStaff.text = "${it.name} - ${it.phone}"
        }
    }

    private fun isValidRangeDate(): Boolean{
        if(mFromDate == null || mToDate == null) return true
        (mFromDate!! > mToDate!!).apply {
            show(binding.tvDateError)
            return !this
        }
    }
}

enum class FilterType {
    NORMAL,
    FILTER_BY_CUSTOMER,
    FILTER_BY_STAFF,
    FILTER_IN_REPORT,
}

interface FilterApmOwner : ViewScopeOwner {
    val filterApmDialog: FilterApmDialog
        get() = with(viewScope) {
            getOr("filter:dialog") {
                FilterApmDialog(context)
            }
        }
}