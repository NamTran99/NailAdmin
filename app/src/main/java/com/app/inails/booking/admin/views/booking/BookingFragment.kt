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
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentBookingBinding
import com.app.inails.booking.admin.exception.setOnSelected
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.AppointmentStatusForm
import com.app.inails.booking.admin.model.ui.CancelAppointmentForm
import com.app.inails.booking.admin.model.ui.HandleAppointmentForm
import com.app.inails.booking.admin.model.ui.StartServiceForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.repository.booking.RemindAppointmentRepository
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.tabs.TabLayout

class BookingFragment : BaseFragment(R.layout.fragment_booking),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner {
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
        viewModel.refresh(type)
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
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<String>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    init {
//        refresh()
    }

    fun refresh(type: Int) = launch(loadingCustom, error) {
        appointmentRepo(type)
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
}





