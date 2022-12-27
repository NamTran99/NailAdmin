package com.app.inails.booking.admin.views.clients.booking

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
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
import com.app.inails.booking.admin.databinding.FragmentBookingStaffBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.clients.BookingClientApi
import com.app.inails.booking.admin.datasource.remote.clients.StaffClientApi
import com.app.inails.booking.admin.exception.currentDatetime
import com.app.inails.booking.admin.exception.toDateUTC
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.model.form.BookingForm
import com.app.inails.booking.admin.model.form.VoucherForm
import com.app.inails.booking.admin.model.ui.client.IStaffClient
import com.app.inails.booking.admin.model.ui.client.IVoucherClient
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.repository.client.AuthenticateRepository
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingArg(
    val services: List<Int> = listOf(),
    val note: String = ""
) : BundleArgument

class BookingStaffFragment : BaseFragment(R.layout.fragment_booking_staff), TopBarOwner {

    private val binding by viewBinding(FragmentBookingStaffBinding::bind)
    private val viewModel by viewModel<BookingStaffVM>()
    private lateinit var mStaffAdapter: StaffAdapter
    private val arg by lazy { BundleArgument.of(arguments) ?: BookingArg() }
    val currentDateTime = currentDatetime().toDateUTC()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_select_staff) { activity?.onBackPressed() })
        with(binding) {
            mStaffAdapter = StaffAdapter(binding.rcvStaff)
            mStaffAdapter.onItemSelectedListener = {
                viewModel.form.hasStaff = true
                swFilterAvailable.isChecked = false
            }
            mStaffAdapter.onImageSelectedListener = {
                viewImagesDialog.show(it)
            }
            swFilterAvailable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.form.hasStaff = false
                    viewModel.isFilterAvailable = isChecked
                    mStaffAdapter.unSelectAll()
                }
            }
            btnSubmit.onClick {
                viewModel.form.run {
                    staffId = mStaffAdapter.selectedItem?.id
                    services = arg.services.toString()
                    datetime = currentDateTime
                    note = arg.note
                }
                summaryBookingDialog.show(
                    mStaffAdapter.selectedItem?.name,
                    currentDateTime,
                    viewModel.serviceList,
                    {
                        it?.let {
                            viewModel.form.voucher = it
                        }
                        viewModel.submit()
                    },
                    { voucher, total ->
                        viewModel.voucherForm.run {
                            code = voucher
                        }
                        viewModel.checkVoucher(total)
                    }, {
                        notificationDialog.show(
                            getString(R.string.label_voucher_information), it
                        )
                    })
            }
        }
        with(viewModel) {
//            authenticate.bind { open<AuthenticateActivity>() }
            staff.bind {
                mStaffAdapter.submit(it)
                binding.rcvStaff.show(it.isNotEmpty())
                binding.txtNoData.show(it.isEmpty())
            }
            success.bind {
                summaryBookingDialog.dismiss()
                Router.run { redirectToSuccess() }
            }
            fetch(currentDateTime)

            voucherResult.bind {
                summaryBookingDialog.updateVoucher(it)
            }
        }
    }
}

class BookingStaffVM(
    private val fetchAllStaffRepo: FetchAllStaffRepository,
    private val createAppointment: CreateAppointmentRepository,
    private val authenticateRepository: AuthenticateRepository,
    private val appCache: AppCache
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {

    var isFilterAvailable: Boolean = false
    val staff = fetchAllStaffRepo.results
    val form = BookingForm()
    val success = SingleLiveEvent<Any>()
    val authenticate = SingleLiveEvent<Any>()
    val serviceList = appCache.servicesSelected ?: listOf()

    fun fetch(dateTimeSelected: String) = launch(loading, error) {
        fetchAllStaffRepo(isFilterAvailable, dateTimeSelected)
    }

    fun submit() = launch(loading, error) {
        form.validate()
        if (authenticateRepository.isLogged())
            success.post(createAppointment(form))
        else authenticate.call()
    }

    val voucherResult = createAppointment.voucherResult
    val voucherForm = VoucherForm()
    fun checkVoucher(total: Float) = launch(loading, error) {
        voucherForm.validate()
        createAppointment.checkVoucher(total, voucherForm)
    }
}

@Inject(ShareScope.Fragment)
class CreateAppointmentRepository(
    private val bookingApi: BookingClientApi,
    private val salonLocalSource: SalonLocalSource,
    private val userLocalSource: UserLocalSource,
    private val bookingFactory: BookingClientFactory
) {

    suspend operator fun invoke(form: BookingForm) {
        form.type = if (userLocalSource.isOwnerLogin()) BookingForm.TYPE_WALK_IN
        else BookingForm.TYPE_APPOINTMENT
        form.salonSlug = salonLocalSource.getSalonSlug()
        salonLocalSource.setAppointmentCurrent(bookingApi.create(form).await())
    }


    val voucherResult = MutableLiveData<IVoucherClient>()
    suspend fun checkVoucher(total: Float, form: VoucherForm) {
        form.salonId = salonLocalSource.getSalonId() as Long
        voucherResult.postValue(
            bookingFactory.createVoucher(
                total,
                bookingApi.checkVoucher(form).await()
            )
        )
    }
}

@Inject(ShareScope.Fragment)
class FetchAllStaffRepository(
    private val staffApi: StaffClientApi,
    private val bookingFactory: BookingClientFactory,
    private val salonLocalSource: SalonLocalSource
) {

    val results = MutableLiveData<List<IStaffClient>>()

    suspend operator fun invoke(isFilterAvailable: Boolean, dateTimeSelected: String) {
        results.post(
            bookingFactory.createStaffs(
                staffApi.alls(salonLocalSource.getSalonId().toString()).await(),
                isFilterAvailable,
                dateTimeSelected
            )
        )
    }
}
