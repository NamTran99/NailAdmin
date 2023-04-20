package com.app.inails.booking.admin.views.management.customer

import FetchListCustomerRepo
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentManageCustomerBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.form.UpdateCustomerForm
import com.app.inails.booking.admin.model.popup.PopUpCustomerMore
import com.app.inails.booking.admin.model.popup.PopUpServiceMore
import com.app.inails.booking.admin.model.ui.CustomerImpl
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupCustomerMoreOwner
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.views.management.customer.adapters.ManageCustomerAdapter
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.sangcomz.fishbun.Fishton.refresh

class ManageCustomerFragment : BaseFragment(R.layout.fragment_manage_customer), TopBarOwner,
      UpdateCustomerDialogOwner, PopupCustomerMoreOwner {
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
            updateCustomerResult.bind{
                success(R.string.update_customer_success)
                updateCustomerDialog.dismiss()
                mAdapter.updateItem(it)
            }
            refreshLoading.bind {
                if(it){
                    binding.emptyLayout.lvEmpty.hide()
                }
                binding.viewRefresh.isRefreshing = it
            }

            listCustomer.bind{
                mAdapter.clear()
                mAdapter.submit(it)
                it.isEmpty() show binding.emptyLayout.lvEmpty
                it.isNotEmpty() show binding.rvCustomers

                 binding.emptyLayout.tvEmptyData.setText(
                     if(binding.searchView.text.isEmpty())
                         R.string.label_empty_data else R.string.no_result_order_found
                 )
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
                onClickCreateAppointment = {
                    appEvent.findingCustomer.post(it to true)
                    Router.redirectToCreateAppointment(self, id = null)
                }
                onClickMenuListener = {view, item ->
                    popup.items = PopUpCustomerMore.mockCustomerMenu(requireContext())
                    popup.setListener {
                        when(it.id){
                            PopUpCustomerMore.EDIT_ID ->{
                                updateCustomerDialog.show(item){ customer ->
                                    viewModel.updateCustomer(customer)
                                }
                            }
                            PopUpCustomerMore.BOOKING_LIST ->{
                                Router.run { redirectToCustomerBookingList(item.id) }
                            }
                        }
                    }
                    popup.run{
                        setupWithViewLeft(view)
                    }
                }
            }
            searchView.onClickSearchAction = {
                refresh(it)
            }
            searchView.setOnSearchListener(
                onLoading = { viewRefresh.isRefreshing = true },
                onSearch = { refresh(it) })
        }

        appEvent.refreshData.observe(this){
            refresh(binding.searchView.text.toString())
        }
    }

    override fun onResume() {
        refresh(binding.searchView.text)
        super.onResume()
    }

    private fun refresh(key: String) {
        viewModel.getListCustomer(key)
    }
}

class ManageCustomerViewModel(
    val fetchListCustomerRepo: FetchListCustomerRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val listCustomer = fetchListCustomerRepo.results
    val updateCustomerResult = fetchListCustomerRepo.updateCustomerResult
    val success = SingleLiveEvent<Any>()

    fun getListCustomer(search: String = "") = launch(refreshLoading, error) {
        fetchListCustomerRepo(search)
    }

    fun updateCustomer(form: UpdateCustomerForm) =  launch(refreshLoading, error) {
        fetchListCustomerRepo.updateCustomer(form)
    }
}