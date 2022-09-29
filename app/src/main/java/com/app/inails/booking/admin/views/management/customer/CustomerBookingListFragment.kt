package com.app.inails.booking.admin.views.management.customer

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentCustomerBookingListBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.booking.*
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerListBookingArg(
    val customerID: Int = 0
) : BundleArgument

class CustomerBookingListFragment : BaseFragment(R.layout.fragment_customer_booking_list),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner, FilterApmOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner {
    private val binding by viewBinding(FragmentCustomerBookingListBinding::bind)
    private val viewModel by viewModel<CustomerBookingListViewModel>()
    private val args by lazy { argument<CustomerListBookingArg>() }
    private lateinit var mAdapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpListener()
        viewModel.getAppointment(args.customerID)
    }

    private fun setUpListener() {
        with(viewModel) {
            listAppointment.bind {
                mAdapter.submit(it)
                it.isNullOrEmpty() show binding.emptyLayout.tvEmptyData
                !it.isNullOrEmpty() show binding.rvServices
            }

            refreshLoading.bind {
                binding.viewRefresh.isRefreshing = it
            }
            success.bind {
                success(it)
            }
            checkInSuccess.bind {
                success("Client check-in success")
                appEvent.changeTabBooking.post(0)
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
            resultCheckIn.bind {
                mAdapter.updateItem(it)
            }
        }

        appEvent.refreshData.observe(this) {
            refreshView()
        }
    }

    private fun setUpView() {
        topBar.setState(
            SimpleTopBarState(
                R.string.title_booking_list,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            binding.emptyLayout.tvEmptyData.text =
                "This customer doesn't booking any appointments yet"
            mAdapter = AppointmentAdapter(rvServices).apply {
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
                        acceptAppointmentDialog.onSelectStaffListener = {
                            Router.redirectToChooseStaff(self, 2, apm.dateAppointment)
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
            }

            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener {
                refreshView()
            }

            tvFilter.onClick {
                showFilterDialog()
            }
            ivFilter.onClick {
                showFilterDialog()
            }
            searchView.setOnSearchListener(onLoading = {
                viewRefresh.isRefreshing = true
                mAdapter.clear()
            },
                onSearch = { refreshView() })
        }
    }

    private fun showFilterDialog() {
        filterApmDialog.show(
            viewModel.filterCustomerForm, type = FilterType.FILTER_BY_CUSTOMER
        ) { form ->
            viewModel.filterCustomerForm.run {
                searchCustomer = form.searchCustomer
                searchStaff = form.searchStaff
                date = form.date
                toDate = form.toDate
            }

            refreshView()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }

    private fun refreshView() {
        mAdapter.clear()
        viewModel.getAppointment(args.customerID, binding.searchView.text.toString())
    }

    private fun showConfirmDialog(title: String, message: String, function: () -> Unit) {
        confirmDialog.show(
            title = title,
            message = message
        ) {
            function.invoke()
        }
    }
}

class CustomerBookingListViewModel(
    private val appointmentRepo: AppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val idRemove = appointmentRepo.resultRemove
    val resultCheckIn = appointmentRepo.resultCheckIn
    val listAppointment = appointmentRepo.results
    val appointment = appointmentRepo.result
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<String>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val filterCustomerForm = AppointmentFilterForm()

    fun getAppointment(customerID: Int, search: String = "") = launch(refreshLoading, error) {
        appointmentRepo.getAppointmentByCustomerID(customerID, search, filterCustomerForm)
    }

    fun updateStatus() = launch(loading, error) {
        appointmentRepo.updateStatusAppointment(form)
        success.post("Finish booking success")
    }

    fun cancel() = launch(loading, error) {
        appointmentRepo.cancelAppointment(formCancel)
        success.post("Cancel success")
    }

    fun remove(id: Int) = launch(loading, error) {
        appointmentRepo.removeAppointment(id)
        success.post("Remove booking success")
    }

    fun handle() = launch(loading, error) {
        appointmentRepo.adminHandleAppointment(formHandle)
        if (formHandle.isAccepted == 1)
            success.post("Accept booking success")
        else
            success.post("Reject booking success")
    }

    fun startService() = launch(loading, error) {
        appointmentRepo.startServiceAppointment(formStartService)
        success.post("Start service success")
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(appointmentRepo.customerWalkIn(id))
    }
}