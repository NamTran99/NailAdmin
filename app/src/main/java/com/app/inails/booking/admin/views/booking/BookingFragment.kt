package com.app.inails.booking.admin.views.booking

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentBookingBinding
import com.app.inails.booking.admin.exception.setOnSelected
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.repository.booking.RemindAppointmentRepository
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.tabs.TabLayout


class BookingFragment : BaseFragment(R.layout.fragment_booking),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner, FilterApmOwner {
    private val binding by viewBinding(FragmentBookingBinding::bind)
    private val viewModel by viewModel<BookingViewModel>()
    private var mType = 1
    private lateinit var mAdapter: AppointmentAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = AppointmentAdapter(binding.rvAppointment)
        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refreshData(mType) }
            btAddAppointment.setOnClickListener {
                Router.redirectToCreateAppointment(self)
            }
            appointTab.addTab(appointTab.newTab().setText(R.string.title_walk_in_customer))
            appointTab.addTab(appointTab.newTab().setText(R.string.title_appointment_customer))
            appointTab.setOnSelected {
                refreshData(it + 1)
            }
            tvFilter.onClick {
                showFilterDialog()
            }
            ivFilter.onClick {
                showFilterDialog()
            }

            btClear.onClick {
                clearFilter()
            }
        }
        with(viewModel) {
            loadingCustom.bind {
                binding.viewRefresh.isRefreshing = it
            }
            appointments.bind(mAdapter.apply {
                onClickItemListener = {
                    Router.redirectToAppointmentDetail(self, it.id)
                }
                onClickCancelListener = { apm ->
                    rejectAppointmentDialog.show(R.string.title_cancel_appointment) {
                        viewModel.formCancel.run {
                            id = apm.id
                            reason = it
                        }
                        viewModel.cancel()
                    }
                }

                onClickRemoveListener = {
                    showConfirmDialog(
                        getString(R.string.title_remove_appointment),
                        String.format(
                            getString(R.string.message_delete_appointment_content),
                            it.id
                        )
                    ) {
                        viewModel.remove(it.id)
                    }
                }

                onClickCusWalkInListener = {
                    showConfirmDialog(
                        getString(R.string.title_customer_check_in),
                        getString(R.string.message_customer_check_in)
                    ) {
                        viewModel.customerWalkIn(it.id)
                    }
                }

                onClickHandleListener = { apm, status ->
                    if (status == 1) {
                        acceptAppointmentDialog.onSelectStaffListener = {
                            Router.redirectToChooseStaff(self, 2, apm.dateTag)
                        }
                        acceptAppointmentDialog.show(apm) { minutes, stffID ->
                            viewModel.formHandle.run {
                                id = apm.id
                                isAccepted = 1
                                workTime = minutes
                                staffId = stffID
                            }
                            viewModel.handle()
                        }
                    }
                    if (status == 0) {
                        rejectAppointmentDialog.show(R.string.title_reject_appointment) {
                            viewModel.formHandle.run {
                                id = apm.id
                                isAccepted = 0
                                reason = it
                            }
                            viewModel.handle()
                        }
                    }
                }
                onClickStartServiceListener = {
                    startServicesDialog.onSelectStaffListener = {
                        Router.redirectToChooseStaff(self, 1)
                    }
                    startServicesDialog.show(it) { staffID, duration ->
                        viewModel.formStartService.run {
                            id = it.id
                            staffId = staffID
                            workTime = duration
                            status = DataConst.AppointmentStatus.APM_IN_PROCESSING
                        }
                        viewModel.startService()
                    }
                }

                onClickFinishListener = {
                    finishBookingDialog.show(it) { amount, notes ->
                        viewModel.form.run {
                            id = it.id
                            price = amount
                            note = notes
                            status = DataConst.AppointmentStatus.APM_FINISH
                        }
                        viewModel.updateStatus()
                    }
                }

                onClickCustomerListener = {
                    customerInfoDialog.show(it)
                }
                onClickRemindListener = {
                    viewModel.remind(it.id)
                }

            }::submit)

            appointments.bind {
                it.isNullOrEmpty() show binding.emptyLayout.tvEmptyData
                !it.isNullOrEmpty() show binding.rvAppointment
            }

            success.bind {
                success(it)
            }

            checkInSuccess.bind {
                success("Client check-in success")
                refreshData(1)
                val tab: TabLayout.Tab? = binding.appointTab.getTabAt(0)
                tab?.select()
            }

            resultCheckIn.bind {
                mAdapter.removeItem(it.id)
            }

            appointment.bind {
                finishBookingDialog.dismiss()
                rejectAppointmentDialog.dismiss()
                startServicesDialog.dismiss()
                acceptAppointmentDialog.dismiss()
                mAdapter.updateItem(it)
            }

            idRemove.bind {
                mAdapter.removeItem(it)
            }

        }

        appActivity.appEvent.chooseStaff.observe(viewLifecycleOwner) {
            startServicesDialog.updateStaff(it)
            acceptAppointmentDialog.updateStaff(it)
        }

        appActivity.appEvent.notifyCloudMessage.observe(viewLifecycleOwner) {
            viewModel.refresh(mType)
        }
        appEvent.refreshData.observe(viewLifecycleOwner) {
            refreshData(mType)
        }
    }

    private fun clearFilter() {
        if (mType == 1)
            viewModel.filterCheckInForm.run {
                searchCustomer = null
                searchStaff = null
                date = null
                toDate = null
            }
        else
            viewModel.filterCustomerForm.run {
                searchCustomer = null
                searchStaff = null
                date = null
                toDate = null
            }
        refreshData(mType)
    }

    private fun showConfirmDialog(title: String, message: String, function: () -> Unit) {
        confirmDialog.show(
            title = title,
            message = message
        ) {
            function.invoke()
        }
    }

    private fun refreshData(type: Int) {
        mType = type
        binding.rvAppointment.removeAllViews()
        displayFilter()
        viewModel.refresh(mType)
    }

    override fun onResume() {
        super.onResume()
        if (mType == 1) {
            val tab: TabLayout.Tab? = binding.appointTab.getTabAt(0)
            tab?.select()
        } else {
            val tab: TabLayout.Tab? = binding.appointTab.getTabAt(1)
            tab?.select()
        }
        refreshData(mType)
    }

    private fun displayFilter() {
        val haveFilter = viewModel.checkHaveFilter(mType)
        (haveFilter) show binding.filterScrollLayout + binding.btClear
        (!haveFilter) show binding.tvFilter
        if (haveFilter) {
            binding.filterContentLayout.removeAllViews()
            if (viewModel.getDateToDate(mType).isNotEmpty()) {
                binding.filterContentLayout.addView(renderSearchItem(viewModel.getDateToDate(mType)))
            }
            if (viewModel.getStaffSearch(mType).isNotEmpty()) {
                binding.filterContentLayout.addView(renderSearchItem(viewModel.getStaffSearch(mType)))
            }

            if (viewModel.getCustomerSearch(mType).isNotEmpty()) {
                binding.filterContentLayout.addView(
                    renderSearchItem(
                        viewModel.getCustomerSearch(
                            mType
                        )
                    )
                )
            }

        }
    }

    private fun renderSearchItem(label: String): TextView {
        val textView = TextView(appActivity)
        textView.text = label
        TextViewCompat.setTextAppearance(textView, R.style.AppTheme_TextView_Medium)
        textView.setTextColor(ContextCompat.getColor(appActivity, R.color.colorPrimary))
        textView.setBackgroundResource(R.drawable.bg_primary_stroke)
        textView.setPadding(15, 8, 15, 4)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 0, 15, 0)
        textView.layoutParams = lp
        return textView
    }

    private fun showFilterDialog() {
        filterApmDialog.show(
            if (mType == 1) viewModel.filterCheckInForm
            else viewModel.filterCustomerForm
        ) { form ->
            if (mType == 1)
                viewModel.filterCheckInForm.run {
                    searchCustomer = form.searchCustomer
                    searchStaff = form.searchStaff
                    date = form.date
                    toDate = form.toDate
                }
            else
                viewModel.filterCustomerForm.run {
                    searchCustomer = form.searchCustomer
                    searchStaff = form.searchStaff
                    date = form.date
                    toDate = form.toDate
                }
            refreshData(mType)
        }
    }

}


class BookingViewModel(
    private val appointmentRepo: AppointmentRepository,
    private val remindAppointmentRepo: RemindAppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointments = appointmentRepo.results
    val idRemove = appointmentRepo.resultRemove
    val appointment = appointmentRepo.result
    val resultCheckIn = appointmentRepo.resultCheckIn
    val form = AppointmentStatusForm()
    val filterCheckInForm = AppointmentFilterForm(type = 1)
    val filterCustomerForm = AppointmentFilterForm(type = 2)
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<String>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    fun refresh(type: Int) = launch(loadingCustom, error) {
        appointmentRepo(if (type == 1) filterCheckInForm else filterCustomerForm)
    }

    fun updateStatus() = launch(loading, error) {
        appointmentRepo.updateStatusAppointment(form)
        success.post("Finish appointment success")
    }

    fun cancel() = launch(loading, error) {
        appointmentRepo.cancelAppointment(formCancel)
        success.post("Cancel appointment success")
    }

    fun remove(id: Int) = launch(loading, error) {
        appointmentRepo.removeAppointment(id)
        success.post("Remove appointment success")
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(appointmentRepo.customerWalkIn(id))
    }

    fun handle() = launch(loading, error) {
        appointmentRepo.adminHandleAppointment(formHandle)
        if (formHandle.isAccepted == 1)
            success.post("Accept appointment success")
        else
            success.post("Reject appointment success")
    }

    fun startService() = launch(loading, error) {
        appointmentRepo.startServiceAppointment(formStartService)
        success.post("Start service success")
    }

    fun remind(id: Int) = launch(loading, error) {
        remindAppointmentRepo(id)
        success.post("Remind customer success")
    }

    fun checkHaveFilter(
        type: Int
    ): Boolean {
        return if (type == 1)
            !filterCheckInForm.date.isNullOrEmpty() || !filterCheckInForm.toDate.isNullOrEmpty() || !filterCheckInForm.searchStaff.isNullOrEmpty() || !filterCheckInForm.searchCustomer.isNullOrEmpty()
        else !filterCustomerForm.date.isNullOrEmpty() || !filterCustomerForm.toDate.isNullOrEmpty() || !filterCustomerForm.searchStaff.isNullOrEmpty() || !filterCustomerForm.searchCustomer.isNullOrEmpty()
    }

    fun getDateToDate(
        type: Int
    ): String {
        if (type == 1) {
            return if (!filterCheckInForm.date.isNullOrEmpty() && !filterCheckInForm.toDate.isNullOrEmpty()) {
                "${
                    filterCheckInForm.date!!.toDateAppointment(
                        format = DatePickerDialog.FORMAT_DATE_API,
                        parseFormat = "MMM dd"
                    )
                } - ${
                    filterCheckInForm.toDate!!.toDateAppointment(
                        format = DatePickerDialog.FORMAT_DATE_API,
                        parseFormat = "MMM dd"
                    )
                }"
            } else if (!filterCheckInForm.date.isNullOrEmpty()) {
                filterCheckInForm.date!!.toDateAppointment(
                    format = DatePickerDialog.FORMAT_DATE_API,
                    parseFormat = "MMM dd"
                )
            } else if (!filterCheckInForm.toDate.isNullOrEmpty()) {
                filterCheckInForm.toDate!!.toDateAppointment(
                    format = DatePickerDialog.FORMAT_DATE_API,
                    parseFormat = "MMM dd"
                )
            } else ""
        } else {
            return if (!filterCustomerForm.date.isNullOrEmpty() && !filterCustomerForm.toDate.isNullOrEmpty()) {
                "${
                    filterCustomerForm.date!!.toDateAppointment(
                        format = DatePickerDialog.FORMAT_DATE_API,
                        parseFormat = "MMM dd"
                    )
                } - ${
                    filterCustomerForm.toDate!!.toDateAppointment(
                        format = DatePickerDialog.FORMAT_DATE_API,
                        parseFormat = "MMM dd"
                    )
                }"
            } else if (!filterCustomerForm.date.isNullOrEmpty()) {
                filterCustomerForm.date!!.toDateAppointment(
                    format = DatePickerDialog.FORMAT_DATE_API,
                    parseFormat = "MMM dd"
                )
            } else if (!filterCustomerForm.toDate.isNullOrEmpty()) {
                filterCustomerForm.toDate!!.toDateAppointment(
                    format = DatePickerDialog.FORMAT_DATE_API,
                    parseFormat = "MMM dd"
                )
            } else ""
        }
    }

    fun getCustomerSearch(
        type: Int
    ): String {
        return if (type == 1) {
            if (!filterCheckInForm.searchCustomer.isNullOrEmpty()) {
                "Customer: ${filterCheckInForm.searchCustomer}"
            } else ""
        } else {
            if (!filterCustomerForm.searchCustomer.isNullOrEmpty()) {
                "Customer: ${filterCustomerForm.searchCustomer}"
            } else ""
        }
    }

    fun getStaffSearch(
        type: Int
    ): String {
        return if (type == 1) {
            if (!filterCheckInForm.searchStaff.isNullOrEmpty()) {
                "Staff: ${filterCheckInForm.searchStaff}"
            } else ""
        } else {
            if (!filterCustomerForm.searchStaff.isNullOrEmpty()) {
                "Customer: ${filterCustomerForm.searchStaff}"
            } else ""
        }
    }
}





