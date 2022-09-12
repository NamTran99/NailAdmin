package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.route.BundleArgument
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
import com.app.inails.booking.admin.databinding.FragmentChooseStaffBinding
import com.app.inails.booking.admin.model.ui.StaffForm
import com.app.inails.booking.admin.repository.auth.StaffRepo
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChooseStaffArg(
    val idSelected: Int? = 0
) : BundleArgument

class ChooseStaffFragment : BaseRefreshFragment(R.layout.fragment_choose_staff), TopBarOwner {
    private val binding by viewBinding(FragmentChooseStaffBinding::bind)
    private val viewModel by viewModel<ChooseStaffViewModel>()
    override fun onRefreshListener() {
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.label_choose_staff
            ) { findNavigator().navigateUp() })
        with(binding) {
            rvStaff.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        with(viewModel) {
            staffs.bind(SelectStaffAdapter(binding.rvStaff).apply {
                onClickItemListener = {
                    form.run {
                        id = it.id
                        phone = it.phone
                        name = it.name
                    }
                    findNavigator().navigateUp(
                        result = form.toBundle()
                    )
                }
            }::submit)
        }
    }

}


class ChooseStaffViewModel(
    private val staffRepo: StaffRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results
    val form = StaffForm()

    init {
        refresh()
    }

    fun refresh() = launch(refreshLoading, error) {
        staffRepo()
    }
}