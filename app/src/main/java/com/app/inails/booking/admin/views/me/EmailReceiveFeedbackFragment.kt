package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class EmailReceiveFeedbackFragment : BaseFragment(R.layout.fragment_email_receive_feedback), TopBarOwner  {
    val viewModel by viewModel<ChangePasswordViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_email_receive_feedback
            ) { activity?.onBackPressed() })
    }

}


class EmailReceiveFeedbackViewModel(
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

