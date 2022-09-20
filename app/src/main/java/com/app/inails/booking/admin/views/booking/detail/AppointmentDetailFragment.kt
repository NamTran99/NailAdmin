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
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentDetailBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.booking.AppointmentDetailRepository
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.booking.*
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentDetailFragment : BaseFragment(R.layout.fragment_appointment_detail),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner {
    private val binding by viewBinding(FragmentAppointmentDetailBinding::bind)
    private val viewModel by viewModel<AppointmentDetailViewModel>()
    private val arg by lazy { argument<Routing.AppointmentDetail>() }
    private var mAppointment: IAppointment? = null

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

            checkInSuccess.bind {
                success("Success")
                appEvent.changeTabBooking.post(0)
            }

            checkInSuccess.bind {
                success("Client check-in success")
                appEvent.changeTabBooking.post(0)
            }

            appointment.bind {
                finishBookingDialog.dismiss()
                rejectAppointmentDialog.dismiss()
                startServicesDialog.dismiss()
                acceptAppointmentDialog.dismiss()
                displays(it)
            }

            idRemove.bind {
                findNavigator().navigateUp()
            }
        }
        setListeners()

    }

    private fun displays(item: IAppointment) {
        mAppointment = item
        with(binding) {
            tvCustomerName.text = item.name
            tvTimeAndDate.text = item.dateAppointment
            tvStaffName.text = item.staffName
            tvID.text = item.id.formatID()
            tvStatus.text = item.statusDisplay
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), item.colorStatus))
            tvTotalAmount.text = item.totalPrice.formatPrice()
            tvNotes.text = item.notes
            (item.status == DataConst.AppointmentStatus.APM_CANCEL ||
                    (item.status == DataConst.AppointmentStatus.APM_WAITING && item.type == 2) ||
                    item.status == DataConst.AppointmentStatus.APM_PENDING) show btDelete
            afterAcceptLayout.show(item.status == DataConst.AppointmentStatus.APM_WAITING && item.type == 1)
            (item.status == DataConst.AppointmentStatus.APM_ACCEPTED && item.type == 2) show acceptLayout
            (item.status == DataConst.AppointmentStatus.APM_PENDING && item.type == 2) show waitingLayout
            (item.status == DataConst.AppointmentStatus.APM_FINISH) show finishLayout

            (ServicePriceAdapter(rvServices)).apply {
                submit(item.serviceList)
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

    private fun setListeners() {
        binding.btCancel.onClick {
            rejectAppointmentDialog.show(R.string.title_cancel_appointment) {
                viewModel.formCancel.run {
                    id = arg.id
                    reason = it
                }
                viewModel.cancel()
            }
        }

        binding.btWalkIn.onClick {
            showConfirmDialog(
                getString(R.string.title_customer_check_in),
                getString(R.string.message_customer_check_in)
            ) {
                viewModel.customerWalkIn(arg.id)
            }
        }

        binding.btAccept.onClick {
            acceptAppointmentDialog.show(mAppointment!!) { minutes, stfID ->
                viewModel.formHandle.run {
                    id = arg.id
                    isAccepted = 1
                    workTime = minutes
                    staffId = stfID

                }
                viewModel.handle()
            }
        }
        binding.btReject.onClick {
            rejectAppointmentDialog.show(R.string.title_reject_appointment) {
                viewModel.formHandle.run {
                    id = arg.id
                    isAccepted = 0
                    reason = it
                }
                viewModel.handle()
            }
        }

        binding.btService.onClick {
            rejectAppointmentDialog.show(R.string.title_reject_appointment) {
                viewModel.formHandle.run {
                    id = arg.id
                    isAccepted = 0
                    reason = it
                }
                viewModel.handle()
            }
        }


        binding.btFinish.onClick {
            if (mAppointment != null)
                finishBookingDialog.show(mAppointment!!) { amount, notes ->
                    viewModel.form.run {
                        id = it.id
                        price = amount
                        note = notes
                        status = DataConst.AppointmentStatus.APM_FINISH
                    }
                    viewModel.updateStatus()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh(arg.id)
    }

}

class AppointmentDetailViewModel(
    private val appointmentDetailRepo: AppointmentDetailRepository,
    private val appointmentRepo: AppointmentRepository,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointment = appointmentDetailRepo.results
    val idRemove = appointmentRepo.resultRemove
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
        success.post(appointmentRepo.updateStatusAppointment(form))
    }

    fun cancel() = launch(loading, error) {
        success.post(appointmentRepo.cancelAppointment(formCancel))
    }

    fun remove(id: Int) = launch(loading, error) {
        success.post(appointmentRepo.removeAppointment(id))
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(appointmentRepo.customerWalkIn(id))
    }

    fun handle() = launch(loading, error) {
        success.post(appointmentRepo.adminHandleAppointment(formHandle))
    }

    fun startService() = launch(loading, error) {
        success.post(appointmentRepo.startServiceAppointment(formStartService))
    }
}



