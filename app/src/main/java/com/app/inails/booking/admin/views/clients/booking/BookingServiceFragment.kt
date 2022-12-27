package com.app.inails.booking.admin.views.clients.booking

import android.os.Bundle
import android.support.core.event.ErrorEvent
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.ErrorLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.core.livedata.post
import android.support.core.route.clear
import android.support.core.route.open
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
import com.app.inails.booking.admin.databinding.FragmentBookingServiceBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.datasource.remote.clients.BookingClientApi
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.factory.client.FetchAllSalonRepository
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.IUser
import com.app.inails.booking.admin.model.ui.client.IServiceClient
import com.app.inails.booking.admin.model.ui.client.ITime
import com.app.inails.booking.admin.model.ui.client.IUserClient
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.client.AuthenticateRepository
import com.app.inails.booking.admin.repository.client.NotificationRepository
import com.app.inails.booking.admin.views.clients.profile.ProfileDisplayRepository
import com.app.inails.booking.admin.views.clients.salon.SalonDetailRepo
import com.app.inails.booking.admin.views.clients.salon.SalonScheduleClientAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog2
import com.app.inails.booking.admin.views.dialog.picker.TimePickerDialog2
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.widget.topbar.ServiceTopBarState
import kotlin.text.Typography.times


class BookingServiceFragment : BaseRefreshFragment(R.layout.fragment_booking_service), TopBarOwner {

    private val binding by viewBinding(FragmentBookingServiceBinding::bind)
    private val viewModel by viewModel<BookingServiceVM>()
    private val mDatePickerDialog by lazy { DatePickerDialog2(appActivity) }
    private val mTimePickerDialog by lazy { TimePickerDialog2(appActivity) }
    private lateinit var mServiceAdapter: ServiceAdapter
    private lateinit var mTimeAdapter: TimeAdapter
    private var mDateSelected: String = ""
    private var mDateTagSelected: String = ""
    private var mTimeSelected: String = ""

    override fun onRefreshListener() {
        viewModel.onLaunch()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            ServiceTopBarState(
                R.string.title_welcome_guest,
                onMenuClick = { appEvent.openDrawer.call() },
                onLogoutClick = { logoutCustomer() },
                onNotifyClick = { Router.run { redirectToNotification() } }
            )
        )
        mServiceAdapter = ServiceAdapter(binding.rcvService)
        mTimeAdapter = TimeAdapter(binding.rcvTime)
        with(binding) {
            txtSalonNameCurrent.onClick {
                Router.open(
                    self,
                    Routing.SalonDetail(binding.txtSalonNameCurrent.tag.toString().toLong())
                )
            }
            listOf(edtNote, btnDate, rcvSchedule).alpha()
            mDatePickerDialog.setupClickWithView(btnDate)
            mTimePickerDialog.setupClickWithView(btnTime) { mTimeAdapter.unSelectAll() }
            btnNext.onClick {
                viewModel.next(
                    mServiceAdapter.selectedItems,
                    btnDate.tag.toString(),
                    btnTime.text.toString()
                )
            }
//            btnAuth.onClick(R.string.btn_sign_in) { open<AuthenticateActivity>() }
//            btnAuth.onClick(R.string.btn_sign_up) {
//                open<AuthenticateActivity>(AuthenticateArg(AuthenticateArg.Type.SIGN_UP))
//            }

            mTimeAdapter.onItemClickListener = {
                btnTime.text = it.time
            }
            btnSchedule.onClick { ivArrow.rotationDropdownWithView(rcvSchedule) }
            mServiceAdapter.onImageSelectedListener = {
                viewImagesDialog.shows(0, it)
            }
        }

        with(viewModel) {
            errorCustom.bind { handle(self, it) }
            salonNameLive.bind {
                binding.txtSalonNameCurrent.text = it.second
                binding.txtSalonNameCurrent.tag = it.first
            }
            scheduleLive.bind {
                SalonScheduleClientAdapter(binding.rcvSchedule, true).submit(it.first)
                binding.btnSchedule.text = it.second
            }
            allUserLive.bind(::configUI)
            services.bind(mServiceAdapter::submit)
            times.bind {
                mTimeAdapter.submit(it)
                binding.btnTime.text = mTimeAdapter.timeSelected?.time
            }
            openStaff.bind {
                Router.run { redirectToStaff(it.first,  binding.edtNote.text.toString()) }
            }
            logoutCustomerSuccess.bind {
//                open<AuthenticateActivity>(AuthenticateArg(AuthenticateArg.Type.CHECK_IN)).clear()
            }
            totalRead.bind { topBar.state<ServiceTopBarState>().showBadge(it) }
        }
        with(appEvent) {
            resetBooking.bind { resetData() }
            refreshServices.bind { viewModel.onLaunch() }
            notifyFetchTotal.bind { viewModel.fetchTotalUnread() }
        }
    }

    private fun resetData() {
        binding.edtNote.setText("")
        mServiceAdapter.unSelectAll()
        mTimeAdapter.unSelectAll()
    }

    private fun logoutCustomer() {
        confirmDialog.show(
            title = R.string.title_customer_logout,
            message = R.string.message_logout_app_for_customer,
            buttonConfirm = R.string.btn_yes_logout
        ) {
            viewModel.logoutCustomer()
        }
    }

    private fun configUI(it: Pair<Boolean, IUserClient?>) {
        val userCustomer = it.second
        topBar.state<ServiceTopBarState>().setTitle(userCustomer?.welcomeName)
        topBar.state<ServiceTopBarState>().showLogoutCustomer(true)
            binding.dateTimeLayout.hide()
        if (userCustomer == null) {
            topBar.state<ServiceTopBarState>().showMenu(false)
            appEvent.enableMenuLeft.post(false)
        } else {
            topBar.state<ServiceTopBarState>().showMenu(true)
            appEvent.enableMenuLeft.post(true)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mDateSelected.isNotEmpty()) {
            binding.btnDate.text = mDateSelected
            binding.btnDate.tag = mDateTagSelected
        }

        if (mTimeSelected.isNotEmpty()) {
            binding.btnTime.text = mTimeSelected
        }

        viewModel.fetchTotalUnread()
    }

    override fun onPause() {
        super.onPause()
        mDateSelected = binding.btnDate.text.toString()
        mDateTagSelected = binding.btnDate.tag.toString()
        mTimeSelected = binding.btnTime.text.toString()
    }
}

class BookingServiceVM(
    private val serviceRepo: ServiceRepository,
    private val timeRepo: TimeRepository,
    profileDisplayRepo: ProfileDisplayRepository,
    private val authRepo: AuthenticateRepository,
    private val salonRepository: FetchAllSalonRepository,
    private val notificationRepo: NotificationRepository,
    private val salonDetailRepo: SalonDetailRepo,
    private val fetchAllSalonRepo: FetchAllSalonRepository,
    private val appCache: AppCache
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val allUserLive = profileDisplayRepo.getAllUserLive()
    val salonNameLive = salonRepository.getSalonLive()
    val scheduleLive = salonRepository.getSalonScheduleLive()
    val services = serviceRepo.results
    val times = timeRepo.results
    val openStaff = SingleLiveEvent<Pair<List<IServiceClient>, String>>()
    val logoutCustomerSuccess = SingleLiveEvent<Any>()
    val totalRead = MutableLiveData<Int>()

    val errorCustom: ErrorEvent = ErrorLiveData()

    init {
        onLaunch()
        fetchTotalUnread()
    }

    fun onLaunch() = launch(refreshLoading, errorCustom) {
        serviceRepo()
        timeRepo()
        val salon=salonDetailRepo(salonRepository.getSalonSelectedID().toString().toLong())
        fetchAllSalonRepo.saveSalonSelected(salon)
    }

    fun fetchTotalUnread() = launch(null, error) {
        totalRead.post(notificationRepo.totalUnread())
    }

    fun next(
        services: List<IServiceClient>,
        date: String,
        time: String?
    ) = launch(loading, errorCustom) {
        validate(services)
        appCache.servicesSelected = services
        val dateTime = if (allUserLive.value?.first == true) "" else "$date $time"
        openStaff.post(services to dateTime)
    }

    private fun validate(
        services: List<IServiceClient>?,
    ) {
        if (services.isNullOrEmpty()) resourceError(R.string.error_blank_service)
    }

    fun logoutCustomer() = launch(loading, error) {
        logoutCustomerSuccess.post(authRepo.logoutCustomer())
    }

}

@Inject(ShareScope.Fragment)
class TimeRepository(private val bookingFactory: BookingClientFactory) {
    val results = MutableLiveData<List<ITime>>()
    suspend operator fun invoke() {
        results.post(bookingFactory.createTimeList())
    }
}

@Inject(ShareScope.Fragment)
class ServiceRepository(
    private val bookingApi: BookingClientApi,
    private val bookingFactory: BookingClientFactory,
    private val userLocalSource: UserLocalSource,

) {
    val results = MutableLiveData<List<IServiceClient>>()
    suspend operator fun invoke() {
        results.post(
            bookingFactory
                .createServiceList(
                    bookingApi.services(userLocalSource.getSalonID().toString())
                        .await()
                )
        )
    }
}
