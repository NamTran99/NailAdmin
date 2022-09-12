package com.app.inails.booking.admin.views.booking

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentBinding
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentFragment(val type: Int) : BaseRefreshFragment(R.layout.fragment_appointment),
    TopBarOwner {
    private val binding by viewBinding(FragmentAppointmentBinding::bind)
    private val viewModel by viewModel<AppointmentViewModel>()

    override fun onRefreshListener() {
        viewModel.refresh(type)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            appointments.bind(AppointmentAdapter(binding.rvAppointment).apply {
                onClickItemListener = {

                }
            }::submit)
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh(type)
    }

}


class AppointmentViewModel(
    private val appointmentRepo: AppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointments = appointmentRepo.results

    init {
//        refresh()
    }

    fun refresh(type: Int) = launch(refreshLoading, error) {
        appointmentRepo(type)
    }
}


@Inject(ShareScope.Fragment)
class AppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<List<IAppointment>>()
    suspend operator fun invoke(type: Int) {
        results.post(
            bookingFactory
                .createAppointmentList(
                    bookingApi.listAppointmentInDashboard(type)
                        .await()
                )
        )
    }
}
