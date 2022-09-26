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
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.management.service.CreateUpdateServiceOwner
import com.app.inails.booking.admin.views.report.adapters.ReportFragmentAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ReportFragment : BaseFragment(R.layout.fragment_report), TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner {

    private val binding by viewBinding(FragmentReportBinding::bind)
    private val viewModel by viewModel<ReportFragmentViewModel>()
    private lateinit var mAdapter: ReportFragmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
        setUpListener()
    }

    private fun setUpListener() {
        with(viewModel) {
            refreshLoading.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }

            listCustomer.bind(mAdapter::submit)
        }
    }


    private fun setUpView() {
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_customer,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refresh() }

            mAdapter = ReportFragmentAdapter(rvReport)
        }
    }

    private fun refresh() {
        mAdapter.clear()
        viewModel.refresh()
    }
}

class ReportFragmentViewModel(
    val appointmentRepository: AppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val listCustomer = appointmentRepository.results.map {
        it?.filter { appointment ->
            appointment.status == APM_FINISH
        }
    }
    val success = SingleLiveEvent<Any>()

    init {
        refresh()
    }

    fun refresh() {
        getListAppointment()
    }

    fun getListAppointment() = launch(refreshLoading, error) {
        appointmentRepository(TYPE_WALK_IN_CUSTOMER)
    }

    companion object {
        const val TYPE_WALK_IN_CUSTOMER = 1
    }
}
