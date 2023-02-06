package com.app.inails.booking.admin.views.me

import android.graphics.Color
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.text.InputFilter
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentVoucherApplyBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.exception.toDateUTC
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.VoucherForm
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.TimePickerWithIntervalDialogOwner
import com.app.inails.booking.admin.views.dialog.picker.TimeType
import com.app.inails.booking.admin.views.widget.DecimalDigitsInputFilter
import com.app.inails.booking.admin.views.widget.MinMaxFilter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner


class VoucherApplyFragment : BaseFragment(R.layout.fragment_voucher_apply), TopBarOwner,
    TimePickerWithIntervalDialogOwner {

    companion object {
        const val REQUEST_KEY = "VoucherApply"
    }

    val viewModel by viewModel<AddVoucherVM>()
    val binding by viewBinding(FragmentVoucherApplyBinding::bind)

    private val mStartTimeDialog by lazy { DatePickerDialog(appActivity) }
    private val mEndTimeDialog by lazy { DatePickerDialog(appActivity) }
    private val args by lazy { argument<Routing.VoucherApply>() }
    private val mStartTimeUTC: String
        get() {
            if (binding.tvStartTime.tag == null) return ""
            return "${binding.tvStartTime.tag} $startTime"
        }

    private val mEndTimeUTC: String
        get() {
            if (binding.tvEndTime.tag == null) return ""
            return "${binding.tvEndTime.tag} $endTime"
        }

    var startTime: String = "8:00 am"
    var endTime: String = "8:00 am"

    var voucherType: Int = 1
        set(value) {
            binding.apply {
                etValue.setText("")
                if (value == 1) {
                    etValue.filters =
                        arrayOf<InputFilter>(DecimalDigitsInputFilter(3, 2), MinMaxFilter(0f, 100f))
                } else {
                    etValue.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 2))
                }
                imgSymbol.setImageResource(if (value == 1) R.drawable.ic_percent else R.drawable.ic_dollar)
                tvVoucherTypeAction.setText(if (value == 1) R.string.percent else R.string.value_1)
                viewModel.voucherForm.type = value
                field = value
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lvStartTime.onClick {
                tvStartTime.callOnClick()
            }
            lvEndTime.onClick {
                tvEndTime.callOnClick()
            }
            imgSymbol.onClick {
                etValue.showKeyboard()
            }
            val displayFormatType =
                if (viewModel.isVnLanguage) DateTimeFormat.format2_vn else DateTimeFormat.format2_en
            viewModel.voucherForm.listExistCode = args.listOfCode
            voucherType = 1
            topBar.setState(
                SimpleTopBarState(
                    R.string.title_voucher_apply,
                    onBackClick = {
                        activity?.onBackPressed()
                    },
                )
            )
            mStartTimeDialog.setupClickWithView(tvStartTime, false)
            binding.tvStartTime.tag = null
            mStartTimeDialog.setPositiveButtonText(R.string.btn_next)

            mStartTimeDialog.onDateListener = {
                timePickerDialog.show(startTime, TimeType.Start)
                tvStartTime.text =
                    mStartTimeUTC.convertAllTimeType(toFormat = displayFormatType)
            }

            mEndTimeDialog.setupClickWithView(tvEndTime, false)
            binding.tvEndTime.tag = null
            mEndTimeDialog.setPositiveButtonText(R.string.btn_next)
            mEndTimeDialog.onDateListener = {
                timePickerDialog.show(endTime, TimeType.End)
                tvEndTime.text = mEndTimeUTC.convertAllTimeType(toFormat = displayFormatType)
            }

            timePickerDialog.onSubmitClick = { time, _, type ->
                if (type == TimeType.Start) {
                    startTime = time
                    tvStartTime.text =
                        mStartTimeUTC.convertAllTimeType(toFormat = displayFormatType)
                } else {
                    endTime = time
                    tvEndTime.text = mEndTimeUTC.convertAllTimeType(toFormat = displayFormatType)
                }
            }

            tvVoucherTypeAction.showPopUp(R.menu.menu_voucher_type) { id ->
                voucherType = when (id) {
                    R.id.typePercent -> 1
                    R.id.typeValue -> 2
                    else -> 1
                }
            }

            spCustomerType.apply {
                val voucherCustomerType = resources.getStringArray(R.array.voucher_customer_type)
                adapter = object : ArrayAdapter<String>(
                    requireContext(),
                    R.layout.layout_spinner_item, voucherCustomerType
                ) {
                    override fun isEnabled(position: Int): Boolean {
                        return position != 0
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view: TextView =
                            super.getDropDownView(position, convertView, parent) as TextView
                        //set the color of first item in the drop down list to gray
                        if (position == 0) {
                            view.setTextColor(Color.GRAY)
                        } else {
                            //here it is possible to define color for other items by
                            //view.setTextColor(Color.RED)
                        }
                        return view
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            (view as TextView).setTextColor(Color.GRAY)
                        } else {
                            (view as TextView).setTextColor(Color.BLACK)
                        }
                        viewModel.voucherForm.type_customer = position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //
                    }
                }
            }

            btSubmit.onClick {
                viewModel.voucherForm.apply {
                    code = etCode.getTextString()
                    startDate = mStartTimeUTC.toDateUTC()
                    endDate = mEndTimeUTC.toDateUTC()
                    valueDiscount = etValue.getTextString()
                    salon_id = userLocalSource.getSalonID() ?: 0
                    description = etDescription.getTextString()
                    // type : voucherType
                    // type_customer: spCustomerType
                }.convertToVoucher()
                viewModel.validateVoucher()
            }
        }

        viewModel.apply {
            success.bind {
                appEvent.voucherApply.post(
                    viewModel.voucherForm
                )
                onBackPress()
            }
        }
    }
}

class AddVoucherVM(
    private val userLocalSource: UserLocalSource
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val voucherForm = VoucherForm()
    val success = SingleLiveEvent<Any>()
    val isVnLanguage = userLocalSource.isVietNamLanguage()
    fun validateVoucher() = launch(loading, error) {
        success.post(
            voucherForm.validate()
        )
    }
}