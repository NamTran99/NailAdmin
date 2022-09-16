package com.app.inails.booking.admin.views.booking.detail

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentDetailBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.drawableStart
import com.app.inails.booking.admin.extention.formatID
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.booking.*
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentDetailFragment : BaseRefreshFragment(R.layout.fragment_appointment_detail),
    TopBarOwner {
    private val binding by viewBinding(FragmentAppointmentDetailBinding::bind)
    private val viewModel by viewModel<AppointmentDetailViewModel>()
    private val arg by lazy { argument<Routing.AppointmentDetail>() }
    override fun onRefreshListener() {
        viewModel.refresh(arg.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_appointment_detail
            ) { activity?.onBackPressed() })
        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { viewModel.refresh(arg.id) }
            btDelete.setOnClickListener {
                showConfirmDialog(
                    getString(R.string.title_remove_appointment),
                    String.format(
                        getString(R.string.message_remove_appointment),
                        arg.id
                    )
                ) {
                    viewModel.remove(arg.id)
                }
            }
        }
        with(viewModel) {
            loadingCustom.bind {
                binding.viewRefresh.isRefreshing = it
            }

            appointment.bind {
                displays(it)
            }

            success.bind {
                success("Success")
            }

            idRemove.bind {
                success("")
                findNavigator().navigateUp()
            }
        }
    }

    private fun displays(item: IAppointment) {
        with(binding) {
            tvCustomerName.text = item.name
            tvServices.text = item.servicesName
            tvTimeAndDate.text = item.dateAppointment
            tvStaffName.text = item.staffName
            tvID.text = item.id.formatID()
            tvStatus.text = item.statusDisplay
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), item.colorStatus))
            (item.status == DataConst.AppointmentStatus.APM_CANCEL || item.status == DataConst.AppointmentStatus.APM_WAITING || item.status == DataConst.AppointmentStatus.APM_PENDING) show btDelete
            afterAcceptLayout.show(item.status == DataConst.AppointmentStatus.APM_WAITING && item.type == 1)
            (item.status == DataConst.AppointmentStatus.APM_ACCEPTED && item.type == 2) show acceptLayout
            (item.status == DataConst.AppointmentStatus.APM_PENDING && item.type == 2) show waitingLayout
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

    override fun onResume() {
        super.onResume()
        viewModel.refresh(arg.id)
    }

}

class AppointmentDetailViewModel(
    private val appointmentDetailRepo: AppointmentDetailRepository,
    private val updateStatusApmRepo: UpdateStatusApmRepository,
    private val cancelAppointmentRepo: CancelAppointmentRepository,
    private val removeAppointmentRepo: RemoveAppointmentRepository,
    private val customerWalkInRepo: CustomerWalkInRepository,
    private val handleAppointmentRepo: HandleAppointmentRepository,
    private val startServiceRepo: StartServiceRepository,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointment = appointmentDetailRepo.results
    val appointUpdate = updateStatusApmRepo.results
    val appointCancel = cancelAppointmentRepo.results
    val idRemove = removeAppointmentRepo.results
    val appointWalkIn = customerWalkInRepo.results
    val appointHandle = handleAppointmentRepo.results
    val appointStartService = startServiceRepo.results
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<Any>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    init {
//        refresh()
    }

    fun refresh(id: Int) = launch(loadingCustom, error) {
        appointmentDetailRepo(id)
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

    fun startService() = launch(loading, error) {
        success.post(startServiceRepo(formStartService))
    }
}



