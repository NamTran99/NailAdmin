package com.app.inails.booking.admin.views.main

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.map
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentStaffListBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.auth.StaffRepo

class StaffListFragment : BaseRefreshFragment(R.layout.fragment_staff_list) {
    private val binding by viewBinding(FragmentStaffListBinding::bind)
    private val viewModel by viewModel<ManageStaffViewModel>()
    private lateinit var mAdapter: StaffStatusAdapter
    override fun onRefreshListener() {
        mAdapter.clear()
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            viewRefresh.colorSchemeDefault()
            mAdapter = StaffStatusAdapter(binding.rvStaff)
            rvStaff.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            mAdapter.onLoadMoreListener = { nexPage, _ ->
                viewModel.refresh(nexPage)
            }
            mAdapter.onClickAppointmentListener = {
                Router.redirectToAppointmentDetail(this@StaffListFragment, it!!.id)
            }

            btClose.setOnClickListener {
                findNavigator().navigateUp()
            }
        }

        with(viewModel) {
            staffs.bind {
                mAdapter.submit(it)
                binding.emptyLayout.tvEmptyData.show(it.isNullOrEmpty())
                binding.rvStaff.show(!it.isNullOrEmpty())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAdapter.clear()
        viewModel.refresh()
    }
}


class ManageStaffViewModel(
    private val staffRepo: StaffRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results.map {
        it?.sortedByDescending {
            it.status
        }?.reversed()
    }

    fun refresh(page: Int = 1) = launch(refreshLoading, error) {
        staffRepo("", page)
    }
}



