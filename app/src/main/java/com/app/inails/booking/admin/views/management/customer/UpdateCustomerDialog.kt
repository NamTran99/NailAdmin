package com.app.inails.booking.admin.views.management.customer

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.app.AppSettingsOwner
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogUpdateCustomerBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.form.UpdateCustomerForm
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.google.android.libraries.places.api.model.Place


class UpdateCustomerDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner,
    AppSettingsOwner {

    private val binding = viewBinding(DialogUpdateCustomerBinding::inflate)
    private var customerType = 2 //2 normal, 3, vip
        set(value) {
            setUINormalType(value == 2)
            field = value
        }
    private val mDatePickerDialog by lazy { DatePickerDialog(context as BaseActivity) }

    init {
        setCancelable(false)
        binding.apply {
            rbGroupType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    rbNormal.id -> customerType = 2
                    rbVip.id -> customerType = 3
                }
            }
            btClose.onClick {
                confirmDialog.show(
                    title = context.getString(R.string.tittle_exit_update_customer),
                    message = context.getString(R.string.message_exit),
                    function = {
                        dismiss()
                    }
                )
            }
            etLocation.onClick {
                appSettings.openPlaceAutoComplete("", ::onPlaceSelected)
            }
            mDatePickerDialog.setupClickWithView(etBirthday, true)
            mDatePickerDialog.setDisablePastDates(false)
            mDatePickerDialog.setDisableFutureDates(true)
            mDatePickerDialog.setDisplayFormat("MM-dd-yyyy")
        }
    }

    private fun onPlaceSelected(place: Place) {
        binding.etLocation.setText(place.address.toString())
    }

    private fun setUINormalType(isNormal: Boolean) {
        binding.apply {
            if (isNormal) {
                rbNormal.setTextColor(context.getColor(R.color.white))
                rbVip.setTextColor(context.getColor(R.color.colorPrimary))
            } else {
                rbNormal.setTextColor(context.getColor(R.color.colorPrimary))
                rbVip.setTextColor(context.getColor(R.color.white))
            }
        }
    }

    fun show(
        customer: ICustomer? = null,
        function: ((UpdateCustomerForm) -> Unit) = {}
    ) {
        with(binding) {
            customer?.let { customer ->
                customerType = customer.type
                rbGroupType.check(if (customer.type == 2) rbNormal.id else rbVip.id)
                etNote.setTextCustom(customer.note)
                etPhone.inputTypePhoneUS()
                etName.setTextCustom(customer.name)
                etPhone.setTextCustom(customer.phone)
                etBirthday.setTextCustom(customer.birthDay)
                etEmail.setTextCustom(customer.email)
                etLocation.setTextCustom(customer.address)
                btSubmit.onClick {
                    val updateCustomerForm = UpdateCustomerForm(
                        id = customer.id,
                        type = customerType,
                        note = etNote.getTextString(),
                        birthday = etBirthday.tag.toString(),
                        email = etEmail.getTextString(),
                        address = etLocation.getTextString(),
                        name = etName.getTextString(),
                        phone = etPhone.getTextString().convertPhoneToNormalFormat()
                    )
                    function.invoke(updateCustomerForm)
                }
            }

            rbGroupType.setOnCheckedChangeListener { radioGroup, i ->
                customerType = when (i) {
                    rbNormal.id -> {
                        2
                    }
                    rbVip.id -> {
                        3
                    }
                    else -> {
                        2
                    }
                }
            }
        }
        super.show()
    }
}

interface UpdateCustomerDialogOwner : ViewScopeOwner {
    val updateCustomerDialog: UpdateCustomerDialog
        get() = with(viewScope) {
            getOr("staff:dialog") { UpdateCustomerDialog(getActivityContext()) }
        }
}