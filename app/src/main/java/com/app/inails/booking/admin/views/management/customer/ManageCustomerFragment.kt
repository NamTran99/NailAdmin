package com.app.inails.booking.admin.views.management.customer

import FetchListCustomerRepo
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentManageCustomerBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.views.management.customer.adapters.ManageCustomerAdapter
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageCustomerFragment : BaseFragment(R.layout.fragment_manage_customer), TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentManageCustomerBinding::bind)
    private val viewModel by viewModel<ManageCustomerViewModel>()
    private lateinit var mAdapter: ManageCustomerAdapter

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
            listCustomer.bind{
                it.isNullOrEmpty() show binding.emptyLayout.tvEmptyData
                !it.isNullOrEmpty() show binding.rvCustomers
            }
        }
    }

    private fun setUpView() {
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_customer,
                onBackClick = {
                    activity?.onBackPressed()
                },
            ) )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refresh(searchView.text.toString()) }

            mAdapter = ManageCustomerAdapter(rvCustomers).apply {
                onClickOpenBookingList = {
                    Router.run { redirectToCustomerBookingList(it.id) }
                }
            }
            searchView.setOnSearchListener(
                onLoading = {
                    viewRefresh.isRefreshing = true
                    mAdapter.clear()
                },
                onSearch = { refresh(it) })
        }

        appEvent.refreshData.observe(this){
            refresh(binding.searchView.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        refresh(binding.searchView.text.toString())
    }

    private fun refresh(key: String) {
        mAdapter.clear()
        viewModel.getListCustomer(key)
    }
}


class ManageCustomerViewModel(
    val fetchListCustomerRepo: FetchListCustomerRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val listCustomer = fetchListCustomerRepo.results
    val success = SingleLiveEvent<Any>()

    init {
        getListCustomer()
    }

    fun getListCustomer(search: String = "") = launch(refreshLoading, error) {
        fetchListCustomerRepo(search)
    }
}




