package com.app.inails.booking.admin.views.main

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
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
import com.app.inails.booking.admin.repository.auth.StaffRepo

class StaffListFragment : BaseRefreshFragment(R.layout.fragment_staff_list) {
    private val binding by viewBinding(FragmentStaffListBinding::bind)
    private val viewModel by viewModel<ManageStaffViewModel>()
    override fun onRefreshListener() {
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            rvStaff.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

            btClose.setOnClickListener {
                findNavigator().navigateUp()
            }
        }

        with(viewModel) {
            staffs.bind(StaffStatusAdapter(binding.rvStaff).apply {
                onClickItemListener = {

                }
            }::submit)
        }
    }

}


class ManageStaffViewModel(
    private val staffRepo: StaffRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results

    init {
        refresh()
    }

    fun refresh() = launch(refreshLoading, error) {
        staffRepo()
    }
}



