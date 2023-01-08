package com.app.inails.booking.admin.views.report

import FetchListCustomerRepo
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.map
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_FINISH
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentReportBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.AppointmentFilterForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.repository.auth.FetchAllStaffRepo
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.booking.AppointmentAdapter
import com.app.inails.booking.admin.views.booking.dialog_filter.FilterApmOwner
import com.app.inails.booking.admin.views.booking.dialog_filter.FilterType
import com.app.inails.booking.admin.views.booking.dialog_filter.SearchCustomerOwner
import com.app.inails.booking.admin.views.booking.dialog_filter.SearchStaffOwner
import com.app.inails.booking.admin.views.management.service.CreateUpdateServiceOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ReportFragment : BaseFragment(R.layout.fragment_report), TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner, FilterApmOwner, SearchStaffOwner,
    SearchCustomerOwner {

    private val binding by viewBinding(FragmentReportBinding::bind)
    private val viewModel by viewModel<ReportFragmentViewModel>()
    private lateinit var mAdapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
        setUpListener()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpListener() {
        with(viewModel) {
            refreshLoading.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }
            listAppointment.bind{
                if(it.first == 1){
                    mAdapter.clear()
                }
                mAdapter.submit(it.second.appointment)

                (mAdapter.itemCount == 0) show binding.emptyLayout.tvEmptyData
                (mAdapter.itemCount > 0) show binding.rvReport

                binding.emptyLayout.tvEmptyData.text = if(binding.searchView.text.isEmpty())
                    "Your salon doesn't have any reports yet" else "There are no results matching your search keyword."

                it.second.totalAppointment.let { size ->
                    binding.tvTotalApm.text =  requireContext().getString(if(size <=1)R.string.number_booking else R.string.number_bookings, size.toString())
                }
                binding.tvTotal.text = "$${it.second.totalPrice}"
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
    }


    private fun setUpView() {
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_report,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refreshView() }
            mAdapter = AppointmentAdapter(rvReport).apply {
                onLoadMoreListener = { nextPage, _ ->
                    viewModel.getListAppointment(nextPage)
                }

                onClickItemListener = {
                    Router.redirectToAppointmentDetail(self, it.id)
                }
            }
            searchView.apply {
                onClickSearchAction = {
                    viewModel.filterCustomerForm.keyword = it
                    refreshView()
                }
                onLayoutFilterClick = {
                    filterApmDialog.show(
                        viewModel.filterCustomerForm,
                        type = FilterType.FILTER_IN_REPORT,
                        openSearchCustomer = {
                            viewModel.loadCustomer("", 1)
                        },
                        openSearchStaff = {
                            viewModel.loadStaff("", 1)
                        }
                    ) { form ->
                        viewModel.filterCustomerForm.setDataFromDialog(form)
                        searchView.showHideImgFilter(viewModel.filterCustomerForm)
                        refreshView()
                    }
                }
            }
        }
        refreshView()
    }

    private fun refreshView() {
        binding.tvTotal.text = totalDefault
        binding.tvTotalApm.text = totalApmDefault
        mAdapter.clear()
        viewModel.getListAppointment()
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }
    companion object {
        const val totalDefault = "$---.--"
        const val totalApmDefault = "( --- bookings )"
    }
}

class ReportFragmentViewModel(
    val appointmentRepository: AppointmentRepository,
    private val customerRepo: FetchListCustomerRepo,
    private val staffRepo: FetchAllStaffRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val filterCustomerForm = AppointmentFilterForm(
        type = null,
        status = APM_FINISH
    )

    val listAppointment = appointmentRepository.resultReport
    val staffList = staffRepo.results
    val customerList = customerRepo.resultWithPage

    fun getListAppointment(page: Int = 1) = launch(refreshLoading, error) {
        appointmentRepository.getApmFinishForReport(filterCustomerForm, page)
    }

    fun loadCustomer(keyword: String, page: Int) = launch(if (page == 1) loading else null, error) {
        customerRepo.search(keyword, page)
    }

    fun loadStaff(keyword: String, page: Int) = launch(if (page == 1) loading else null, error) {
        staffRepo(keyword, page)
    }

    companion object {
        const val TYPE_WALK_IN_CUSTOMER = 1
    }
}
