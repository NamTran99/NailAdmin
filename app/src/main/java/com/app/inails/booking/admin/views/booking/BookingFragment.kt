package com.app.inails.booking.admin.views.booking

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.viewmodel.launch
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.base.BaseRefreshFragment

class BookingFragment : BaseRefreshFragment(R.layout.fragment_booking) {
    override fun onRefreshListener() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}

//
//class UserViewModel(
//    private val fetchAllUsersRepo: FetchAllUsersRepo
//) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
//    val users = fetchAllUsersRepo.result
//
//    init {
//        refresh()
//    }
//
//    fun refresh() = launch(refreshLoading, error) {
//        fetchAllUsersRepo()
//    }
//}

