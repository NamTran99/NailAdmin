package com.app.inails.booking.admin.views.report

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.map
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_FINISH
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentReportBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.AppointmentFilterForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.booking.AppointmentAdapter
import com.app.inails.booking.admin.views.booking.FilterApmOwner
import com.app.inails.booking.admin.views.management.service.CreateUpdateServiceOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ReportFragment : BaseFragment(R.layout.fragment_report), TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner, FilterApmOwner {

    private val binding by viewBinding(FragmentReportBinding::bind)
    private val viewModel by viewModel<ReportFragmentViewModel>()
    private lateinit var mAdapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
        setUpListener()
    }

    private fun setUpListener() {
        with(viewModel) {
            refreshLoading.bind {
                binding.viewRefresh.isRefreshing = it
            }
            listCustomer.bind(mAdapter::submit)
            total.bind{
                binding.tvTotal.text = it.formatPrice()
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
                onClickItemListener = {
                    Router.redirectToAppointmentDetail(self, it.id)
                }
            }
            tvFilter.onClick {
                showFilterDialog()
            }
            ivFilter.onClick {
                showFilterDialog()
            }
        }
        refreshView()
    }

    private fun refreshView() {
        binding.tvTotal.text = totalDefault
        mAdapter.clear()
        viewModel.refresh()
    }

    private fun showFilterDialog() {
        filterApmDialog.show(
            viewModel.filterCustomerForm
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

    companion object{
        const val totalDefault = "$---.--"
    }
}

class ReportFragmentViewModel(
    val appointmentRepository: AppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val filterCustomerForm = AppointmentFilterForm(type = TYPE_WALK_IN_CUSTOMER)

    val listCustomer = appointmentRepository.results.map {
        it?.filter { appointment ->
            appointment.status == APM_FINISH
        }
    }

    val total = listCustomer.map {
        it?.fold(0.0){total, item ->
            total + item.price
        }
    }
    val success = SingleLiveEvent<Any>()

    fun refresh() {
        getListAppointment()
    }

    private fun getListAppointment() = launch(refreshLoading, error) {
        appointmentRepository(filterCustomerForm)
    }

    companion object {
        const val TYPE_WALK_IN_CUSTOMER = 1
    }
}
