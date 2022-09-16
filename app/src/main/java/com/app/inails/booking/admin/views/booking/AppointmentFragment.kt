package com.app.inails.booking.admin.views.booking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.navigation.FragmentResultCallback
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.booking.*
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentFragment(val type: Int) : BaseFragment(R.layout.fragment_appointment),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner, FragmentResultCallback {
    private val binding by viewBinding(FragmentAppointmentBinding::bind)
    private val viewModel by viewModel<AppointmentViewModel>()
    private lateinit var mAdapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = AppointmentAdapter(binding.rvAppointment)
        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { viewModel.refresh(type) }
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
                            getString(R.string.message_remove_appointment),
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
                        acceptAppointmentDialog.show {
                            viewModel.formHandle.run {
                                id = apm.id
                                isAccepted = 1
                                workTime = it
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

                onClickCallListener = {
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel: ${it.phone}")
                    startActivity(dialIntent)
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
                success("Success")
            }

            checkInSuccess.bind {
                success("Success")
                appEvent.changeTabBooking.post(0)
            }

            appointUpdate.bind {
                finishBookingDialog.dismiss()
                mAdapter.updateItem(it)
            }

            appointCancel.bind {
                rejectAppointmentDialog.dismiss()
                mAdapter.updateItem(it)
            }

            idRemove.bind {
                mAdapter.removeItem(it)
            }

            appointWalkIn.bind {
                mAdapter.removeItem(it.id)
            }

            appointHandle.bind {
                acceptAppointmentDialog.dismiss()
                rejectAppointmentDialog.dismiss()
                mAdapter.updateItem(it)
            }

            appointStartService.bind {
                startServicesDialog.dismiss()
                mAdapter.updateItem(it)
            }
        }

        startServicesDialog.onSelectStaffListener = {
            Router.run {
                redirectToChooseStaff()
            }
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

    override fun onResume() {
        super.onResume()
        viewModel.refresh(type)
    }

    override fun onFragmentResult(result: Bundle) {
        val staffForm = result.get("staff") as StaffForm
        startServicesDialog.updateStaff(staffForm)
    }

}


class AppointmentViewModel(
    private val appointmentRepo: AppointmentRepository,
    private val updateStatusApmRepo: UpdateStatusApmRepository,
    private val cancelAppointmentRepo: CancelAppointmentRepository,
    private val removeAppointmentRepo: RemoveAppointmentRepository,
    private val customerWalkInRepo: CustomerWalkInRepository,
    private val handleAppointmentRepo: HandleAppointmentRepository,
    private val startServiceRepo: StartServiceRepository,
    private val remindAppointmentRepo: RemindAppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointments = appointmentRepo.results
    val appointUpdate = updateStatusApmRepo.results
    val appointCancel = cancelAppointmentRepo.results
    val idRemove = removeAppointmentRepo.results
    val appointWalkIn = customerWalkInRepo.results
    val appointHandle = handleAppointmentRepo.results
    val appointStartService = startServiceRepo.results
    val remind = remindAppointmentRepo.results
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<Any>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    init {
//        refresh()
    }

    fun refresh(type: Int) = launch(loadingCustom, error) {
        appointmentRepo(type)
    }

    fun updateStatus() = launch(loading, error) {
        success.post(updateStatusApmRepo(form))
    }

    fun cancel() = launch(loading, error) {
        success.post(cancelAppointmentRepo(formCancel))
    }

    fun remove(id: Int) = launch(loading, error) {
        success.post(removeAppointmentRepo(id))
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(customerWalkInRepo(id))
    }

    fun handle() = launch(loading, error) {
        success.post(handleAppointmentRepo(formHandle))
    }

    fun startService() = launch(loading, error) {
        success.post(startServiceRepo(formStartService))
    }

    fun remind(id: Int) = launch(loading, error) {
        success.post(remindAppointmentRepo(id))
    }
}



