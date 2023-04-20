package com.app.inails.booking.admin.views.clients.booking

import android.os.Bundle
import android.os.CountDownTimer
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentBookingSuccessBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.model.ui.client.IBooking
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToEnterPhone
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.notification.NotificationsManagerClient
import com.app.inails.booking.admin.repository.client.AuthenticateRepository
import com.app.inails.booking.admin.views.clients.appointment.AppointmentFragment
import com.app.inails.booking.admin.views.widget.topbar.NoTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class BookingSuccessFragment : BaseFragment(R.layout.fragment_booking_success), TopBarOwner {

    companion object {
        const val COUNT_DOWN = 15
    }

    private val binding by viewBinding(FragmentBookingSuccessBinding::bind)
    private val viewModel by viewModel<AppointmentSuccessVM>()
    private val notificationsManager by lazy { NotificationsManagerClient(requireContext()) }
    private val countDown = object : CountDownTimer((COUNT_DOWN * 1000).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tvTimer.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            viewModel.logOut()
        }
    }

    override fun onStop() {
        super.onStop()
        countDown.cancel()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(NoTopBarState())
        viewModel.booking.bind(::displays)
        with(viewModel) {
            logoutSuccess.bind {
                notificationsManager.cancelAll()
                redirectToEnterPhone()
            }
        }
    }

    private fun displays(item: IBooking?) = with(binding) {


        ivDone.startAnimVector()
        countDown.start()
        viewBooking.run {
            txtBookingID.text = item?.bookingIdDisplay
            txtSalonName.text = item?.salonName
            txtClientName.text = item?.customerName
            txtPhoneNumber.text = item?.customerPhone
            txtSalonName.onClick { Router.open(self, Routing.SalonDetail(item?.salonID!!)) }
            txtPriceDiscount.text = item?.discount
            txtDiscount.show(item?.showPercent == true)
            txtDiscount.text = item?.percent
            txtTotal.text = item?.totalPrice
            txtTotalAmount.text = item?.totalAmount
            voucherLayout.show(item?.hasVoucher == true)
        }
        tvTimer.text = COUNT_DOWN.toString()
        ServiceSummaryAdapter(rcvService).submit(item?.services)
        txtRsDatetime.text = item?.dateTime
        txtRsStaff.text = item?.staffName
        btnBackToHome.onClick {
            appEvent.resetBooking.call()
            findNavigator().popBackStack(BookingServiceFragment::class, true)
        }
        btnAppointments.onClick {
            findNavigator().popBackStack(BookingServiceFragment::class, true)
            findNavigator().navigate(AppointmentFragment::class)
        }
        btnInfo.onClick {
            notificationDialog.show(
                getString(R.string.label_voucher_information), item?.voucherInfo ?: ""
            )
        }
        tvLogout.onClick{
            countDown.cancel()
            viewModel.logOut()
        }
    }
}

class AppointmentSuccessVM(private val bookingRepo: FetchBookingCurrentRepository,
                           private val authenticateRepository: AuthenticateRepository
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    val booking = bookingRepo.result
    val logoutSuccess = SingleLiveEvent<Int>()

    init {
        launch(loading, error) {
            bookingRepo()
        }
    }

    fun logOut() = launch(loading, error) {
        authenticateRepository.logout()
        logoutSuccess.call()
    }
}

@Inject(ShareScope.Fragment)
class FetchBookingCurrentRepository(
    private val appCache: AppCache,
    private val bookingFactory: BookingClientFactory,
) {

    val result = MutableLiveData<IBooking>()
    operator fun invoke() {
        result.post(bookingFactory.createBooking(appCache.bookingCurrent))
    }
}
