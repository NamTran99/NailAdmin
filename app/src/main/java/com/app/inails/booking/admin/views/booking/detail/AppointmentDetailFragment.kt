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
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.booking.AppointmentDetailRepository
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.booking.*
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentDetailFragment : BaseFragment(R.layout.fragment_appointment_detail),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner, StaffInfoDialogOwner {
    private val binding by viewBinding(FragmentAppointmentDetailBinding::bind)
    private val viewModel by viewModel<AppointmentDetailViewModel>()
    private val arg by lazy { argument<Routing.AppointmentDetail>() }
    private var mAppointment: IAppointment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_appointment_detail, showEdit = false,
                onBackClick = {
                    activity?.onBackPressed()
                },
                onEditClick = {
                    Router.redirectToCreateAppointment(self, arg.id)
                }
            ))

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { viewModel.refresh(arg.id) }
            btDelete.setOnClickListener {
                showConfirmDialog(
                    getString(R.string.title_remove_appointment),
                    String.format(
                        getString(R.string.message_delete_appointment_content),
                        arg.id
                    )
                ) {
                    viewModel.remove(arg.id)
                }
            }
            tvCustomerName.setOnClickListener {
                mAppointment?.let {
                    customerInfoDialog.show(it.customer!!)
                }
            }

            tvStaffName.setOnClickListener {
                mAppointment?.staff?.let {
                    staffInfoDialog.show(it)
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
                success(it)
            }

            appointmentAction.bind {
                finishBookingDialog.dismiss()
                rejectAppointmentDialog.dismiss()
                startServicesDialog.dismiss()
                acceptAppointmentDialog.dismiss()
                displays(it)
            }

            checkInSuccess.bind {
                success("Client check-in success")
            }

            appointment.bind {
                displays(it)
            }

            appointmentCheckIn.bind {
                displays(it)
            }


            idRemove.bind {
                appActivity.onBackPressed()
            }
        }
        appEvent.chooseStaffInDetailAppointment.observe(this) {
            if (it != null) {
                startServicesDialog.updateStaff(it)
                acceptAppointmentDialog.updateStaff(it)
            }
        }

        appEvent.refreshData.observe(this) {
            viewModel.refresh(arg.id)
        }
        setListeners()
    }

    private fun displays(item: IAppointment) {
        mAppointment = item
        with(binding) {
            tvCustomerName.text = item.customerName
            tvTimeAndDate.text = item.dateAppointment
            tvStaffName.text = item.staffName
            tvID.text = item.id.formatID()
            tvStatus.text = item.statusDisplay
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), item.colorStatus))
            tvTotalAmount.text = item.price.formatPrice()
            tvNotes.text = item.noteFinish
            tvNotes.show(!item.noteFinish.isNullOrEmpty())
            (item.status == DataConst.AppointmentStatus.APM_CANCEL || item.status == DataConst.AppointmentStatus.APM_FINISH) show btDelete
            afterAcceptLayout.show((item.status == DataConst.AppointmentStatus.APM_WAITING || item.status == DataConst.AppointmentStatus.APM_IN_PROCESSING) && item.type == 1)
            (item.status == DataConst.AppointmentStatus.APM_ACCEPTED && item.type == 2) show acceptLayout
            (item.status == DataConst.AppointmentStatus.APM_PENDING && item.type == 2) show waitingLayout
            (item.status == DataConst.AppointmentStatus.APM_FINISH) show finishLayout
            (item.status == DataConst.AppointmentStatus.APM_CANCEL) show cancelLayout
            val list = item.serviceList.toMutableList()
            item.serviceCustomObj?.let { list.add(it) }
            (ServicePriceAdapter(rvServices)).apply {
                submit(list)
            }
            btService.isEnabled = item.status != DataConst.AppointmentStatus.APM_IN_PROCESSING
            btService.drawableStart(if (item.status != DataConst.AppointmentStatus.APM_IN_PROCESSING) R.drawable.circle_blue else R.drawable.ic_check_white)
            if (item.status == DataConst.AppointmentStatus.APM_IN_PROCESSING) {
                btFinish.setBackgroundResource(R.drawable.button_gray_green_corner_20)
                btFinish.isEnabled = true
                btFinish.setTextColor(
                    ContextCompat.getColorStateList(
                        appActivity,
                        R.color.color_button_status
                    )
                )
            } else {
                btFinish.isEnabled = false
            }

            tvTypeCancel.text = item.canceledBy
            tvReason.text = item.reasonCancel
            feedbackLayout.show(item.hasFeedback)
            tvFeedbackContent.text = item.feedbackContent
            ratingBar.rating = item.feedbackRating.toFloat()
            tvPhone.text = item.phone.formatPhoneUS()
            tvCreatedAt.text = item.createAt
            tvAppointmentNote.text = item.notes

            (item.notes.trim().isNotEmpty()) show lvAppointmentNote
            item.staff?.let {
                tvStaffName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue02
                    )
                )
            }

            topBar.setState(
                SimpleTopBarState(
                    R.string.title_appointment_detail,
                    showEdit = item.status != DataConst.AppointmentStatus.APM_FINISH && item.status != DataConst.AppointmentStatus.APM_CANCEL && item.status != DataConst.AppointmentStatus.APM_IN_PROCESSING,
                    onBackClick = {
                        activity?.onBackPressed()
                    },
                    onEditClick = {
                        Router.redirectToCreateAppointment(self, arg.id)
                    }
                ))
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
            acceptAppointmentDialog.onSelectStaffListener = {
                Router.redirectToChooseStaff(self, 3, mAppointment!!.dateTag)
            }
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
            mAppointment?.let {
                startServicesDialog.onSelectStaffListener = {
                    Router.redirectToChooseStaff(self, 3)
                }
                startServicesDialog.show(it) { staffID, duration ->
                    viewModel.formStartService.run {
                        id = it.id!!
                        staffId = staffID
                        workTime = duration
                        status = DataConst.AppointmentStatus.APM_IN_PROCESSING
                    }
                    viewModel.startService()
                }
            }
        }

        binding.btFinish.onClick {
            if (mAppointment != null)
                finishBookingDialog.show(mAppointment!!) { amount, notes ->
                    viewModel.form.run {
                        id = mAppointment!!.id
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
    val appointment = appointmentDetailRepo.result
    val appointmentAction = appointmentRepo.result
    val appointmentCheckIn = appointmentRepo.resultCheckIn
    val idRemove = appointmentRepo.resultRemove
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<String>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    init {
//        refresh()
    }

    fun refresh(id: Int) = launch(loadingCustom, error) {
        appointmentDetailRepo(id)
    }

    fun updateStatus() = launch(loading, error) {
        appointmentRepo.updateStatusAppointment(form)
        success.post("Finish appointment success")
    }

    fun cancel() = launch(loading, error) {
        appointmentRepo.cancelAppointment(formCancel)
        success.post("Cancel appointment success")
    }

    fun remove(id: Int) = launch(loading, error) {
        appointmentRepo.removeAppointment(id)
        success.post("Remove appointment success")
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(appointmentRepo.customerWalkIn(id))
    }

    fun handle() = launch(loading, error) {
        appointmentRepo.adminHandleAppointment(formHandle)
        if (formHandle.isAccepted == 1)
            success.post("Accept appointment success")
        else
            success.post("Reject appointment success")
    }

    fun startService() = launch(loading, error) {
        appointmentRepo.startServiceAppointment(formStartService)
        success.post("Start service success")
    }
}



