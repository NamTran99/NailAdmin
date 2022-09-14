package com.app.inails.booking.admin.views.booking

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
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
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentBinding
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.ui.AppointmentStatusForm
import com.app.inails.booking.admin.model.ui.CancelAppointmentForm
import com.app.inails.booking.admin.model.ui.HandleAppointmentForm
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentFragment(val type: Int) : BaseFragment(R.layout.fragment_appointment),
    TopBarOwner, CancelAppointmentOwner, AcceptAppointmentOwner, RejectAppointmentOwner {
    private val binding by viewBinding(FragmentAppointmentBinding::bind)
    private val viewModel by viewModel<AppointmentViewModel>()
    private lateinit var mAdapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = AppointmentAdapter(binding.rvAppointment)
        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { viewModel.refresh(type) }
        }
        with(viewModel) {
            loadingCustom.bind {
                binding.viewRefresh.isRefreshing = it
            }
            appointments.bind(mAdapter.apply {
                onClickItemListener = {

                }
                onClickCancelListener = {
                    cancelAppointmentDialog.show { cancelBy, content ->
                        viewModel.formCancel.run {
                            id = it.id
                            canceledBy = cancelBy
                            reason = content
                        }
                        viewModel.cancel()
                    }
                }

                onClickRemoveListener = {
                    showConfirmDialog(
                        getString(R.string.title_remove_appointment),
                        String.format(
                            getString(R.string.message_remove_appointment),
                            it.id
                        )
                    ) {
                        viewModel.remove(it.id)
                    }
                }

                onClickCusWalkInListener = {
                    showConfirmDialog(
                        getString(R.string.title_customer_check_in),
                        getString(R.string.message_customer_check_in)
                    ) {
                        viewModel.customerWalkIn(it.id)
                    }
                }

                onClickHandleListener = { apm, status ->
                    if (status == 1) {
                        acceptAppointmentDialog.show {
                            viewModel.formHandle.run {
                                id = apm.id
                                isAccepted = 1
                                workTime = it
                            }
                            viewModel.handle()
                        }
                    }
                    if (status == 0) {
                        rejectAppointmentDialog.show {
                            viewModel.formHandle.run {
                                id = apm.id
                                isAccepted = 0
                                reason = it
                            }
                            viewModel.handle()
                        }
                    }

                }
            }::submit)

            appointments.bind {
                it.isNullOrEmpty() show binding.emptyLayout.tvEmptyData
                !it.isNullOrEmpty() show binding.rvAppointment
            }

            success.bind {
                success("Success")
            }

            checkInSuccess.bind {
                success("Success")
                appEvent.changeTabBooking.post(0)
            }

            appointUpdate.bind {
                mAdapter.updateItem(it)
            }

            appointCancel.bind {
                mAdapter.updateItem(it)
            }

            idRemove.bind {
                cancelAppointmentDialog.dismiss()
                mAdapter.removeItem(it)
            }

            appointWalkIn.bind {
                mAdapter.removeItem(it.id)
            }

            appointHandle.bind {
                acceptAppointmentDialog.dismiss()
                rejectAppointmentDialog.dismiss()
                mAdapter.updateItem(it)
            }

        }

    }

    private fun showConfirmDialog(title: String, message: String, function: () -> Unit) {
        confirmDialog.show(
            title = title,
            message = message
        ) {
            function.invoke()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh(type)
    }

}


class AppointmentViewModel(
    private val appointmentRepo: AppointmentRepository,
    private val updateStatusApmRepo: UpdateStatusApmRepository,
    private val cancelAppointmentRepo: CancelAppointmentRepository,
    private val removeAppointmentRepo: RemoveAppointmentRepository,
    private val customerWalkInRepo: CustomerWalkInRepository,
    private val handleAppointmentRepo: HandleAppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointments = appointmentRepo.results
    val appointUpdate = updateStatusApmRepo.results
    val appointCancel = cancelAppointmentRepo.results
    val idRemove = removeAppointmentRepo.results
    val appointWalkIn = customerWalkInRepo.results
    val appointHandle = handleAppointmentRepo.results
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val success = SingleLiveEvent<Any>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    init {
//        refresh()
    }

    fun refresh(type: Int) = launch(loadingCustom, error) {
        appointmentRepo(type)
    }

    fun updateStatus() = launch(loading, error) {
        success.post(updateStatusApmRepo(form))
    }

    fun cancel() = launch(loading, error) {
        success.post(cancelAppointmentRepo(formCancel))
    }

    fun remove(id: Int) = launch(loading, error) {
        success.post(removeAppointmentRepo(id))
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(customerWalkInRepo(id))
    }

    fun handle() = launch(loading, error) {
        success.post(handleAppointmentRepo(formHandle))
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

@Inject(ShareScope.Fragment)
class UpdateStatusApmRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: AppointmentStatusForm) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.updateStatusAppointment(form)
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class CustomerWalkInRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(id: Int) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.customerWalkIn(id)
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class HandleAppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: HandleAppointmentForm) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.adminHandleAppointment(form)
                        .await()
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class CancelAppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: CancelAppointmentForm) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.cancelAppointment(form)
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class RemoveAppointmentRepository(
    private val bookingApi: BookingApi
) {
    val results = MutableLiveData<Int>()
    suspend operator fun invoke(id: Int) {
        bookingApi.removeAppointment(id)
            .await()
        results.post(
            id
        )
    }
}


