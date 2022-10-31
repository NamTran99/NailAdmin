package com.app.inails.booking.admin.views.management.customer

import FetchListCustomerRepo
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
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.auth.FetchAllStaffRepo
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.repository.booking.RemindAppointmentRepository
import com.app.inails.booking.admin.views.booking.*
import com.app.inails.booking.admin.views.booking.dialog_filter.FilterApmOwner
import com.app.inails.booking.admin.views.booking.dialog_filter.FilterType
import com.app.inails.booking.admin.views.booking.dialog_filter.SearchCustomerOwner
import com.app.inails.booking.admin.views.booking.dialog_filter.SearchStaffOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerListBookingArg(
    val id: Int = 0,
    val idType: TypeID = TypeID.Customer
) : BundleArgument

enum class TypeID {
    Customer, Staff
}

// This fragment use for display Booking List of "Customer" and History Apm of "Staff"
class UserBookingListFragment : BaseFragment(R.layout.fragment_customer_booking_list),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner, FilterApmOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner, SearchStaffOwner,
    SearchCustomerOwner {
    private val binding by viewBinding(FragmentCustomerBookingListBinding::bind)
    private val viewModel by viewModel<CustomerBookingListViewModel>()
    private val args by lazy { argument<CustomerListBookingArg>() }
    private lateinit var mAdapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUpDataUI(args)
        setUpView()
        setUpListener()
    }

    private fun setUpListener() {
        with(viewModel) {
            filterForm.apply {
                type = null
                searchCustomer = if (args.idType == TypeID.Customer) args.id else null
                searchStaff = if (args.idType == TypeID.Staff) args.id else null
            }
            listAppointment.bind {
                if(it.first == 1){
                    mAdapter.clear()
                }
                mAdapter.submit(it.second)
                binding.emptyLayout.tvEmptyData.text = if (binding.searchView.text.isEmpty())
                    customerListBookingUI.emptyData else "There are no results matching your search keyword."
                (mAdapter.itemCount == 0) show binding.emptyLayout.tvEmptyData
                (mAdapter.itemCount > 0) show binding.rvAppointment
            }

            refreshLoading.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }
            success.bind {
                success(it)
                refreshView()
            }
            checkInSuccess.bind {
                success("Client check-in success")
                refreshView()
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
            staffList.bind {
                if (it.first == 1) {
                    searchStaffDialog.onLoadMoreListener = { keyword, page ->
                        viewModel.loadStaff(keyword, page)
                    }
                    searchStaffDialog.onSearchListener = { keyword ->
                        viewModel.loadStaff(keyword, 1)
                    }
                    searchStaffDialog.show(it.second) {
                        filterApmDialog.updateStaff(it)
                    }
                } else {
                    searchStaffDialog.addList(it.second)
                }
            }

            customerList.bind {
                if (it.first == 1) {
                    searchCustomerDialog.onLoadMoreListener = { keyword, page ->
                        viewModel.loadCustomer(keyword, page)
                    }
                    searchCustomerDialog.onSearchListener = { keyword ->
                        viewModel.loadCustomer(keyword, 1)
                    }
                    searchCustomerDialog.show(it.second) {
                        filterApmDialog.updateCustomer(it)
                    }
                } else {
                    searchCustomerDialog.addList(it.second)
                }
            }
        }

        appActivity.appEvent.chooseStaff.observe(viewLifecycleOwner) {
            startServicesDialog.updateStaff(it)
            acceptAppointmentDialog.updateStaff(it)
        }

        appEvent.refreshData.observe(this) {
            refreshView()
        }

        appEvent.chooseStaffInDetailAppointment.observe(this) {
            if (it != null) {
                startServicesDialog.updateStaff(it)
                acceptAppointmentDialog.updateStaff(it)
            }
        }
    }

    private fun setUpView() {
        topBar.setState(
            SimpleTopBarState(
                viewModel.customerListBookingUI.titleTopBar,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )
        with(binding) {
            searchView.setHint(viewModel.customerListBookingUI.searchHint)
            mAdapter = AppointmentAdapter(rvAppointment).apply {
                onLoadMoreListener = { nextPage, _ ->
                    viewModel.getAppointment(nextPage)
                }

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
                            Router.redirectToChooseStaff(self, 3, apm.dateTag)
                        }
                        acceptAppointmentDialog.show(apm) { minutes, staffID ->
                            viewModel.formHandle.run {
                                id = apm.id
                                isAccepted = 1
                                workTime = minutes
                                staffId = staffID
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
            }

            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener {
                refreshView()
            }

            searchView.apply {
                onClickSearchAction = {
                    viewModel.filterForm.keyword = it
                    refreshView()
                }
                onLayoutFilterClick = {
                    filterApmDialog.show(
                        viewModel.filterForm,
                        type = if (args.idType == TypeID.Customer) FilterType.FILTER_BY_CUSTOMER else FilterType.FILTER_BY_STAFF,
                        openSearchCustomer = {
                            viewModel.loadCustomer("", 1)
                        },
                        openSearchStaff = {
                            viewModel.loadStaff("", 1)
                        }
                    ) { form ->
                        viewModel.filterForm.setDataFromDialog(form)
                        searchView.showHideImgFilter(viewModel.filterForm)
                        refreshView()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }

    private fun refreshView() {
        mAdapter.clear()
        viewModel.getAppointment()
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
    private val appointmentRepo: AppointmentRepository,
    private val customerRepo: FetchListCustomerRepo,
    private val remindAppointmentRepo: RemindAppointmentRepository,
    private val staffRepo: FetchAllStaffRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val customerListBookingUI = CustomerBookingListUI()
    val idRemove = appointmentRepo.resultRemove
    val resultCheckIn = appointmentRepo.resultCheckIn
    val listAppointment = appointmentRepo.results1
    val appointment = appointmentRepo.result
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<String>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val filterForm = AppointmentFilterForm()
    val staffList = staffRepo.results
    val customerList = customerRepo.resultWithPage

    fun setUpDataUI(customerListBookingArg: CustomerListBookingArg) {
        customerListBookingUI.setUpData(customerListBookingArg)
    }

    fun getAppointment(page: Int = 1) = launch(refreshLoading, error) {
        if (customerListBookingUI.idType == TypeID.Customer) appointmentRepo.getCustomerBookingList(
            filterForm,
            page
        )
        else appointmentRepo.getStaffBookingList(filterForm, page)
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

    fun remind(id: Int) = launch(loading, error) {
        remindAppointmentRepo(id)
        success.post("Remind customer success")
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(appointmentRepo.customerWalkIn(id))
        success.post("Remind customer success")
    }

    fun loadCustomer(keyword: String, page: Int) = launch(if (page == 1) loading else null, error) {
        customerRepo.search(keyword, page)
    }

    fun loadStaff(keyword: String, page: Int) = launch(if (page == 1) loading else null, error) {
        staffRepo(keyword, page)
    }
}

data class CustomerBookingListUI(
    var customerId: Int? = null,
    var staffId: Int? = null,
    var idType: TypeID = TypeID.Customer,
    var searchHint: Int = 0,
    var emptyData: String = "",
    var titleTopBar: Int = 0,
) {

    fun setUpData(customerListBookingArg: CustomerListBookingArg) {
        idType = customerListBookingArg.idType
        if (customerListBookingArg.idType == TypeID.Customer) {
            customerId = customerListBookingArg.id
            searchHint = R.string.hint_search_booking_list
            emptyData = "This customer doesn't booking any appointments yet"
            titleTopBar = R.string.title_booking_list
        } else {
            staffId = customerListBookingArg.id
            searchHint = R.string.hint_search_order_history
            emptyData = "This staff doesn't have any orders yet"
            titleTopBar = R.string.title_order_history
        }
    }
}
