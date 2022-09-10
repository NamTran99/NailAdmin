package com.app.inails.booking.admin.views.booking

import android.os.Bundle
import android.support.core.view.viewBinding
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentBookingBinding
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToBooking
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class BookingFragment : BaseRefreshFragment(R.layout.fragment_booking) , TopBarOwner {
    private val binding by viewBinding(FragmentBookingBinding::bind)
    override fun onRefreshListener() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btAddAppointment.setOnClickListener {
            Router.run { redirectToCreateAppointment() }
        }
    }


}

//
//
//class BookingViewModel(
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

//
//@Inject(ShareScope.Fragment)
//class FetchAppointmentRepository(
//    private val staffApi: StaffApi,
//    private val bookingFactory: BookingFactory,
//    private val salonLocalSource: SalonLocalSource
//) {
//
//    val results = MutableLiveData<List<IStaff>>()
//
//    suspend operator fun invoke(isFilterAvailable: Boolean) {
//        results.post(
//            bookingFactory.createStaffs(
//                staffApi.alls(salonLocalSource.getSalonId().toString()).await(),
//                isFilterAvailable
//            )
//        )
//    }
//}

