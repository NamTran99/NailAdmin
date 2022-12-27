package com.app.inails.booking.admin.views.clients.booking

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
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
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.extention.startAnimVector
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.model.ui.client.IBooking
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.clients.appointment.AppointmentFragment
import com.app.inails.booking.admin.views.widget.topbar.NoTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class BookingSuccessFragment : BaseFragment(R.layout.fragment_booking_success), TopBarOwner {

    private val binding by viewBinding(FragmentBookingSuccessBinding::bind)
    private val viewMode by viewModel<AppointmentSuccessVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(NoTopBarState())
        viewMode.booking.bind(::displays)
    }

    private fun displays(item: IBooking?) = with(binding) {
        ivDone.startAnimVector()
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
        btnInfo.onClick{
            notificationDialog.show(getString(R.string.label_voucher_information),item?.voucherInfo?:""
            )
        }
    }
}

class AppointmentSuccessVM(private val bookingRepo: FetchBookingCurrentRepository) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    val booking = bookingRepo.result

    init {
        launch(loading, error) {
            bookingRepo()
        }
    }
}

@Inject(ShareScope.Fragment)
class FetchBookingCurrentRepository(
    private val appCache: AppCache,
    private val bookingFactory: BookingClientFactory
) {

    val result = MutableLiveData<IBooking>()
    operator fun invoke() {
        result.post(bookingFactory.createBooking(appCache.bookingCurrent))
    }

}
