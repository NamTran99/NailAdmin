//package com.app.inails.booking.admin.views.report
//
//import FetchListCustomerRepo
//import android.os.Bundle
//import android.support.core.event.LiveDataStatusOwner
//import android.support.core.event.WindowStatusOwner
//import android.support.core.livedata.SingleLiveEvent
//import android.support.viewmodel.launch
//import android.view.View
//import androidx.lifecycle.ViewModel
//import com.app.inails.booking.admin.R
//import com.app.inails.booking.admin.base.BaseRefreshFragment
//import com.app.inails.booking.admin.extention.colorSchemeDefault
//import com.app.inails.booking.admin.model.ui.ServiceImpl
//import com.app.inails.booking.admin.navigate.Router
//import com.app.inails.booking.admin.navigate.Router.Companion.redirectToCustomerBookingList
//import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
//import com.app.inails.booking.admin.views.management.customer.adapters.ManageCustomerAdapter
//import com.app.inails.booking.admin.views.management.service.CreateUpdateServiceOwner
//import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
//import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
//
//class ReportFragment:  BaseRefreshFragment(R.layout.fragment_manage_service), TopBarOwner,
//    CreateUpdateServiceOwner, PopupServiceMoreOwner {
//    override fun onRefreshListener() {
////        viewModel.search()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setUpView()
//        setUpListener()
//    }
//
//    private fun setUpListener(){
//            refreshLoading.bind{
//                mAdapter.isLoading = it
//                binding.viewRefresh.isRefreshing = it
//            }
//
//            listCustomer.bind(mAdapter::submit)
//        }
//    }
//
//    private fun setUpView() {
//        topBar.setState(
//            SimpleTopBarState(
//                R.string.mn_manage_customer
//            ) { activity?.onBackPressed() })
//
//        with(binding) {
//            viewRefresh.colorSchemeDefault()
//            viewRefresh.setOnRefreshListener { refresh(searchView.text.toString()) }
//
//            mAdapter = ManageCustomerAdapter(rvCustomers).apply {
//                onClickOpenBookingList = {
//                    com.app.inails.booking.admin.navigate.Router.run { redirectToCustomerBookingList(it.listService as List<ServiceImpl>) }
//                }
//            }
//        }
//    }
//}
//
//class ReportFragmentViewModel(
//    val fetchListCustomerRepo: FetchListCustomerRepo
//) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
//
//    val listCustomer = fetchListCustomerRepo.results
//    val success = SingleLiveEvent<Any>()
//
//    init {
//        refresh()
//    }
//
//    fun refresh() {
//        getListCustomer()
//    }
//
//    fun getListCustomer() = launch(refreshLoading, error) {
//        fetchListCustomerRepo()
//    }
//}
//
//
//
