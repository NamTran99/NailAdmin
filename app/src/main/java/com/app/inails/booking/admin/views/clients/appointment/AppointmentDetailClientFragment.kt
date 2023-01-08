package com.app.inails.booking.admin.views.clients.appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.core.route.lazyArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentDetailClientBinding
import com.app.inails.booking.admin.datasource.remote.clients.BookingClientApi
import com.app.inails.booking.admin.datasource.remote.sockets.SocketClient
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.factory.client.NotificationOptionRepository
import com.app.inails.booking.admin.model.form.ApmCancelForm
import com.app.inails.booking.admin.model.ui.client.IAppointmentClient
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.client.CancelAppointmentRepo
import com.app.inails.booking.admin.repository.client.RemoveAppointmentRepo
import com.app.inails.booking.admin.repository.client.StatusAppointmentRepo
import com.app.inails.booking.admin.views.clients.booking.ServiceSummaryAdapter
import com.app.inails.booking.admin.views.clients.dialog.booking.ApmCancelDialogOwner
import com.app.inails.booking.admin.views.clients.feedbacks.ImageFeedbackAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentDetailClientFragment :
    BaseRefreshFragment(R.layout.fragment_appointment_detail_client),
    TopBarOwner, ApmCancelDialogOwner {

    private val args by lazyArgument<Routing.AppointmentDetailClient>()
    private val viewModel by viewModel<AppointmentDetailVM>()
    private val binding by viewBinding(FragmentAppointmentDetailClientBinding::bind)

    override fun onResume() {
        viewModel.detail(args.idBooking)
        viewModel.readNotification(args.idNotification)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSocket()
    }

    override fun onRefreshListener() {
        viewModel.detail(args.idBooking)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_appointment_detail)
        { activity?.onBackPressed() })
        with(viewModel) {
            details.bind(::displays)
            cancelled.bind(::detail)
            removed.bind { activity?.onBackPressed() }
            checkIn.bind { viewModel.detail(args.idBooking) }
            readSuccess.bind { appEvent.refreshNotifications.call() }
        }
        appEvent.notifyForAppointment.bind { viewModel.detail(args.idBooking) }
    }


    private fun showCancelDialog() {
        apmCancelDialog.show {
            viewModel.form.apply {
                id = args.idBooking
                reason = it
            }
            viewModel.cancel()
        }
    }

    private fun showDeleteDialog(idDisplay: String) {
        confirmDialog.showDelete(
            R.string.title_delete_appointment,
            getString(R.string.message_delete_appointment, idDisplay)
        ) {
            viewModel.remove(args.idBooking)
        }
    }

    private fun displays(item: IAppointmentClient) = with(binding) {
        viewNoData.hide()
        viewContents.run {
            tvCode.text = item.voucherCode
            txtBookingID.text = item.idDisplay
            txtSalonName.text = item.salonName
            txtDatetime.text = item.dateTime
            txtRequest.text = item.staffName
            txtCancelBy.text = item.canceledBy
            txtStatus.apply {
                setText(item.status.first)
                setTextColor(context.colorResource(item.status.second))
                setDrawableStart(item.status.third)
            }
            ServiceSummaryAdapter(rcvService).submit(item.services)
            btnCancel.show(item.isShowCancelButton)
            cancelLayout.show(item.isShowCancelNote)
            txtReason.text = item.reasonCancel
            voucherLayout.show(item.hasVoucher)
            noteLayout.show(item.isShowNote)
            txtCreateAt.text = item.createAtDisplay
            txtTotalAmount.text = item.totalAmount
            txtNote.text = item.finishNote
            finishLayout.show(item.isFinish)
            btnFeedback.show(!item.hasFeedBack)
            btnDirect.show(item.isShowDirect)
            feedbackLayout.show(item.hasFeedBack)
            imageBeforeLayout.show(item.imagesBefore?.isNotEmpty() == true)
            imageAfterLayout.show(item.imagesAfter?.isNotEmpty() == true)
            txtFeedbackContent.text = item.feedbackContent
            ratingBar.rating = item.feedbackRating.toFloat()
            txtSomethingElse.text = item.note
            txtPriceDiscount.text = item.discount
            txtDiscount.show(item.showPercent)
            txtDiscount.text = item.percent
            txtTotal.text = item.totalPrice
            btnCancel.onClick { showCancelDialog() }
            btnFeedback.onClick {
                Router.redirectToSubmitFeedback(
                    self,
                    args.idBooking,
                    args.salonID
                )
            }
            btnDelete.onClick { showDeleteDialog(item.idDisplay) }
            txtSalonName.onClick {
                Router.navigate(self, Routing.SalonDetail(item.salonID))
            }
            btnDirect.onClick { appSettings.navigateMyLocationWithGoogleMap(item.lat, item.lng) }
            ImageFeedbackAdapter(rcvFeedbackImages).apply {
                submit(item.feedbackImages)
                onImageSelectedListener = {
                    viewImagesDialog.shows(it, item.feedbackImages!!)
                }
            }
            ImageFeedbackAdapter(rcvImagesBefore).apply {
                submit(item.imagesBefore)
                onImageSelectedListener = {
                    viewImagesDialog.shows(it, item.imagesBefore!!)
                }
            }
            ImageFeedbackAdapter(rcvImagesAfter).apply {
                submit(item.imagesAfter)
                onImageSelectedListener = {
                    viewImagesDialog.shows(it, item.imagesAfter!!)
                }
            }

            btnInfo.onClick {
                notificationDialog.show(
                    getString(R.string.label_voucher_information), item.voucherInfo
                )
            }
        }
    }
}

class AppointmentDetailVM(
    private val appointmentDetailRepo: AppointmentDetailRepo,
    private val cancelAppointmentRepo: CancelAppointmentRepo,
    private val removeAppointmentRepo: RemoveAppointmentRepo,
    private val notificationOptionRepository: NotificationOptionRepository,
    statusAppointmentRepo: StatusAppointmentRepo,
    private val client: SocketClient
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    val form = ApmCancelForm()
    val cancelled = SingleLiveEvent<Long>()
    val removed = SingleLiveEvent<Long>()
    val checkIn = statusAppointmentRepo.checkInStatus
    val readSuccess = SingleLiveEvent<Any>()
    val test = appointmentDetailRepo.test
    val details = appointmentDetailRepo.result

    init {
        client.connectIfNeed()
        appointmentDetailRepo.abc()
    }

    fun detail(bookingID: Long) = launch(refreshLoading, error) {
        appointmentDetailRepo(bookingID)
    }


    fun cancel() = launch(loading, error) {
        cancelAppointmentRepo(form)
        cancelled.post(form.id)
    }

    fun remove(bookingID: Long) = launch(loading, error) {
        removeAppointmentRepo(bookingID)
        removed.post(bookingID)
    }

    fun disconnectSocket() = launch(null) {
        client.disconnect()
    }

    fun readNotification(idNotification: Long?) = launch(null) {
        if (idNotification == null) return@launch
        readSuccess.post(notificationOptionRepository.read(idNotification))
    }

}

@Inject(ShareScope.Fragment)
class AppointmentDetailRepo(
    private val bookingApi: BookingClientApi,
    private val bookingFactory: BookingClientFactory
) {
    val test = MutableLiveData<IAppointmentClient>()
    val result = MutableLiveData<IAppointmentClient>()
    fun abc() {
        test.post(object : IAppointmentClient {})
    }

    suspend operator fun invoke(bookingID: Long) {
        val rs = bookingApi.detail(bookingID).await()
        result.post(bookingFactory.createAppointment(rs))
    }
}
