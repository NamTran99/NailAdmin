package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.view.viewBinding
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentChangePasswordBinding
import com.app.inails.booking.admin.databinding.FragmentCreateAppointmentBinding
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ChangePasswordFragment : BaseFragment(R.layout.fragment_change_password), TopBarOwner  {
    val viewModel by viewModel<ChangePasswordViewModel>()
    val binding by viewBinding(FragmentChangePasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_change_password
            ) { activity?.onBackPressed() })
    }
}


class ChangePasswordViewModel(
//    private val fetchAllUsersRepo: FetchAllUsersRepo
) : ViewModel(){
//    val users = fetchAllUsersRepo.result
//
//    init {
//        refresh()
//    }
//
//    fun refresh() = launch(refreshLoading, error) {
//        fetchAllUsersRepo()
//    }
}

