package com.app.inails.booking.admin.views.booking

import FetchListCustomerRepo
import FetchListServiceRepo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.combine1
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentBookingBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.FetchAllStaffRepo
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.repository.booking.RemindAppointmentRepository
import com.app.inails.booking.admin.views.booking.dialog_filter.FilterApmOwner
import com.app.inails.booking.admin.views.booking.dialog_filter.SearchCustomerOwner
import com.app.inails.booking.admin.views.booking.dialog_filter.SearchStaffOwner
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs1
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BookingFragment : BaseFragment(R.layout.fragment_booking),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner, FilterApmOwner, SearchCustomerOwner,
    SearchStaffOwner {

    private val binding by viewBinding(FragmentBookingBinding::bind)
    private val viewModel by viewModel<BookingViewModel>()
    private var mType = 1
    private lateinit var mAdapter: AppointmentAdapter
    private val args by lazy { argument<Routing.BookingFragment>() }

    private var beforeImagePath = ArrayList<AppImage>()
    private var afterImagePath = ArrayList<AppImage>()

    private val startForResultBeforeImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                beforeImagePath =
                    ArrayList(pathImage.map { pathUri -> AppImage(image = pathUri.toString()) })
                finishBookingDialog.updateBeforeImages(beforeImagePath)
            }
        }

    private val startForResultAfterImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                afterImagePath =
                    ArrayList(pathImage.map { pathUri -> AppImage(image = pathUri.toString()) })
                finishBookingDialog.updateAfterImages(afterImagePath)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btAddAppointment.show(args.type == Routing.BookingFragment.TypeBooking.APPOINTMENTS)
        finishBookingDialog.apply {
            onSearchService = {
                viewModel.searchService(it)
            }
            onAddBeforeImage = {
                FishBun.with(this@BookingFragment)
                    .setImageAdapter(GlideAdapter())
                    .setMaxCount(5)
                    .setMinCount(1)
                    .setSelectedImages(ArrayList(beforeImagePath.map { it.image.toUri() }))
                    .setActionBarColor(
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        true
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .startAlbumWithActivityResultCallback(startForResultBeforeImage)
            }

            onAddAfterImage = {
                FishBun.with(this@BookingFragment)
                    .setImageAdapter(GlideAdapter())
                    .setMaxCount(5)
                    .setMinCount(1)
                    .setSelectedImages(ArrayList(afterImagePath.map { it.image.toUri() }))
                    .setActionBarColor(
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        true
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .startAlbumWithActivityResultCallback(startForResultAfterImage)
            }

            onclickRemoveAfterImage = { image ->
                afterImagePath.removeAll { it.image == image.image }
            }

            onclickRemoveBeforeImage = { image ->
                beforeImagePath.removeAll { it.image == image.image }
            }
        }
        mAdapter = AppointmentAdapter(binding.rvAppointment).apply {
            onItemImageClick = { list, position ->
                {
                    Router.run { redirectToShowZoomImage1(ShowZoomImageArgs1(list, position)) }
                }
            }
            onLoadMoreListener = { page, _ ->
                viewModel.refresh(mType, page)
            }
            onClickItemListener = {
                Router.redirectToAppointmentDetail(self, it.id)
            }
            onClickCancelListener = { apm ->
                rejectAppointmentDialog.show(R.string.title_cancel_appointment) {
                    viewModel.formCancel.run {
                        id = apm.id
                        reason = it
                    }
                    viewModel.cancel()
                }
            }

            onClickRemoveListener = {
                showConfirmDialog(
                    getString(R.string.title_remove_appointment),
                    String.format(
                        getString(R.string.message_delete_appointment_content),
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
                    acceptAppointmentDialog.onSelectStaffListener = {
                        Router.redirectToChooseStaff(self, 2, apm.dateTag)
                    }
                    acceptAppointmentDialog.show(apm) { minutes, stffID ->
                        viewModel.formHandle.run {
                            id = apm.id
                            isAccepted = 1
                            workTime = minutes
                            staffId = stffID
                        }
                        viewModel.handle()
                    }
                }
                if (status == 0) {
                    rejectAppointmentDialog.show(R.string.title_reject_appointment) {
                        viewModel.formHandle.run {
                            id = apm.id
                            isAccepted = 0
                            reason = it
                        }
                        viewModel.handle()
                    }
                }
            }
            onClickStartServiceListener = {
                startServicesDialog.onSelectStaffListener = {
                    Router.redirectToChooseStaff(self, 1)
                }
                startServicesDialog.show(it) { staffID, duration ->
                    viewModel.formStartService.run {
                        id = it.id
                        staffId = staffID
                        workTime = duration
                        status = DataConst.AppointmentStatus.APM_IN_PROCESSING
                    }
                    viewModel.startService()
                }
            }

            onClickFinishListener = {
                afterImagePath.clear()
                beforeImagePath.clear()
                finishBookingDialog.show(it) { amount, notes, moreServices ->
                    viewModel.form.run {
                        id = it.id
                        note = notes
                        status = DataConst.AppointmentStatus.APM_FINISH
                        beforeImages = beforeImagePath
                        afterImages = afterImagePath
                        moreService = moreServices
                    }
                    viewModel.updateStatus()
                }
            }

            onClickCustomerListener = {
                customerInfoDialog.show(it)
            }
            onClickRemindListener = {
                viewModel.remind(it.id)
            }
        }
        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refreshData(mType) }
            btAddAppointment.setOnClickListener {
                Router.redirectToCreateAppointment(self)
            }
//            appointTab.addTab(appointTab.newTab().setText(R.string.title_walk_in_customer))
//            appointTab.addTab(appointTab.newTab().setText(R.string.title_appointment_customer))
//            appointTab.setOnSelected {
//                refreshData(it + 1)
//            }

            searchView.apply {
                onClickSearchAction = {
                    if (mType == 1) viewModel.filterCheckInForm.keyword =
                        it else viewModel.filterCustomerForm.keyword =
                        it
                    refreshData(mType)
                }
                onLayoutFilterClick = {
                    showFilterDialog()
                }
            }
        }

        with(viewModel) {
            serviceLoadingResult.bind {
                finishBookingDialog.updateServiceSearch(it)
            }
            loadingCustom.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }
            appointments.bind {
                if (it.first == 1) {
                    mAdapter.clear()
                }
                mAdapter.submit(it.second)
                (mAdapter.itemCount == 0) show binding.emptyLayout.lvEmpty
                (mAdapter.itemCount > 0) show binding.rvAppointment
            }

            success.bind {
                success(it)
                refresh(mType)
            }

            checkInSuccess.bind {
                success(R.string.client_check_in_success)
                refreshData(1)
                Router.open(
                    this@BookingFragment,
                    Routing.BookingFragment(Routing.BookingFragment.TypeBooking.CHECK_IN)
                )
            }

            resultCheckIn.bind {
                mAdapter.removeItem(it.id)
            }

            appointment.bind {
                finishBookingDialog.dismiss()
                rejectAppointmentDialog.dismiss()
                startServicesDialog.dismiss()
                acceptAppointmentDialog.dismiss()
                mAdapter.updateItem(it)
            }

            idRemove.bind {
                mAdapter.removeItem(it)
            }

            customerList.bind {
                if (it.first == 1) {
                    searchCustomerDialog.onLoadMoreListener = { keyword, page ->
                        viewModel.loadCustomer(keyword, page)
                    }
                    searchCustomerDialog.onSearchListener = { keyword ->
                        viewModel.loadCustomer(keyword, 1)
                    }
                    searchCustomerDialog.show(it.second) {
                        filterApmDialog.updateCustomer(it)
                    }
                } else {
                    searchCustomerDialog.addList(it.second)
                }

            }

            staffList.bind {
                if (it.first == 1) {
                    searchStaffDialog.onLoadMoreListener = { keyword, page ->
                        viewModel.loadStaff(keyword, page)
                    }
                    searchStaffDialog.onSearchListener = { keyword ->
                        viewModel.loadStaff(keyword, 1)
                    }
                    searchStaffDialog.show(it.second) {
                        filterApmDialog.updateStaff(it)
                    }
                } else {
                    searchStaffDialog.addList(it.second)
                }

            }

        }

        appActivity.appEvent.chooseStaff.observe(viewLifecycleOwner) {
            startServicesDialog.updateStaff(it)
            acceptAppointmentDialog.updateStaff(it)
        }

        appActivity.appEvent.notifyCloudMessageClient.observe(viewLifecycleOwner) {
            viewModel.refresh(mType)
        }
        appEvent.refreshData.observe(viewLifecycleOwner) {
            refreshData(mType)
        }

        setUpData()
    }

    private fun setUpData() {
        mType = args.type.ordinal?.plus(1) ?: 1
        topBar.setState(
            SimpleTopBarState(
                if (mType == 1) R.string.title_customer_check_in else R.string.title_appointment_customer,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )
        refreshData(mType)
    }

    private fun showConfirmDialog(title: String, message: String, function: () -> Unit) {
        confirmDialog.show(
            title = title,
            message = message
        ) {
            function.invoke()
        }
    }

    private fun refreshData(type: Int) {
        mAdapter.clear()
        mType = type
        binding.rvAppointment.removeAllViews()
        displayFilter()
        viewModel.refresh(mType)
    }

    override fun onResume() {
        super.onResume()
//        if (mType == 1) {
//            val tab: TabLayout.Tab? = binding.appointTab.getTabAt(0)
//            tab?.select()
//        } else {
//            val tab: TabLayout.Tab? = binding.appointTab.getTabAt(1)
//            tab?.select()
//        }
        refreshData(mType)
    }

    private fun displayFilter() {
        binding.searchView.showHideImgFilter(viewModel.findFilterFormByType(mType).isHaveDataForFilter())
        val keyword = viewModel.getKeyword(mType)
        binding.searchView.setText(keyword)
//        (haveFilter) show binding.filterScrollLayout + binding.btClear
//        (!haveFilter) show binding.tvFilter
//        if (haveFilter) {
//            binding.filterContentLayout.removeAllViews()
//            if (viewModel.getDateToDate(mType).isNotEmpty()) {
//                binding.filterContentLayout.addView(renderSearchItem(viewModel.getDateToDate(mType)))
//            }
//            if (viewModel.getStaffSearch(mType).isNotEmpty()) {
//                binding.filterContentLayout.addView(renderSearchItem(viewModel.getStaffSearch(mType)))
//            }
//
//            if (viewModel.getCustomerSearch(mType).isNotEmpty()) {
//                binding.filterContentLayout.addView(
//                    renderSearchItem(
//                        viewModel.getCustomerSearch(
//                            mType
//                        )
//                    )
//                )
//            }
//
//        }
    }

//    private fun renderSearchItem(label: String): TextView {
//        val textView = TextView(appActivity)
//        textView.text = label
//        TextViewCompat.setTextAppearance(textView, R.style.AppTheme_TextView_Medium)
//        textView.setTextColor(ContextCompat.getColor(appActivity, R.color.colorPrimary))
//        textView.setBackgroundResource(R.drawable.bg_primary_stroke)
//        textView.setPadding(15, 8, 15, 4)
//        val lp = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        lp.setMargins(0, 0, 15, 0)
//        textView.layoutParams = lp
//        return textView
//    }

    private fun showFilterDialog() {
        filterApmDialog.show(
            if (mType == 1) viewModel.filterCheckInForm
            else viewModel.filterCustomerForm,
            openSearchCustomer = {
                viewModel.loadCustomer("", 1)
            },
            openSearchStaff = {
                viewModel.loadStaff("", 1)
            }
        ) { form ->
            if (mType == 1)
                viewModel.filterCheckInForm = form
            else
                viewModel.filterCustomerForm = form
            refreshData(mType)
        }
    }

}


class BookingViewModel(
    private val appointmentRepo: AppointmentRepository,
    private val remindAppointmentRepo: RemindAppointmentRepository,
    private val customerRepo: FetchListCustomerRepo,
    private val staffRepo: FetchAllStaffRepo,
    private val fetchListServiceRepo: FetchListServiceRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val searchScope = CoroutineScope(Dispatchers.Main)
    val serviceListResult = fetchListServiceRepo.results
    val serviceLoadingResult =
        serviceListResult.combine1(refreshLoading as MutableLiveData<Boolean>) { items, loading ->
            items to loading
        }

    val appointments = appointmentRepo.results1
    val idRemove = appointmentRepo.resultRemove
    val appointment = appointmentRepo.result
    val resultCheckIn = appointmentRepo.resultCheckIn
    val customerList = customerRepo.resultWithPage
    val staffList = staffRepo.results
    val form = AppointmentStatusForm()
    var filterCheckInForm = AppointmentFilterForm(type = 1)
    var filterCustomerForm = AppointmentFilterForm(type = 2)
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<Int>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    fun refresh(type: Int, page: Int = 1) = launch(loadingCustom, error) {
        appointmentRepo(if (type == 1) filterCheckInForm else filterCustomerForm, page, this)
    }

    fun searchService(keyword: String = "", page: Int = 1) =
        launch(refreshLoading, error, searchScope.coroutineContext, true) {
            fetchListServiceRepo(keyword, page)
        }

    fun updateStatus() = launch(loading, error) {
        appointmentRepo.updateStatusAppointment(form)
        success.post(R.string.success_finish_appointment)
    }

    fun cancel() = launch(loading, error) {
        appointmentRepo.cancelAppointment(formCancel)
        success.post(R.string.success_cancel_appointment)
    }

    fun remove(id: Int) = launch(loading, error) {
        appointmentRepo.removeAppointment(id)
        success.post(R.string.success_remove_appointment)
    }

    fun customerWalkIn(id: Int) = launch(loading, error) {
        checkInSuccess.post(appointmentRepo.customerWalkIn(id))
    }

    fun handle() = launch(loading, error) {
        appointmentRepo.adminHandleAppointment(formHandle)
        if (formHandle.isAccepted == 1)
            success.post(R.string.success_accept_appointment)
        else
            success.post(R.string.success_reject_appointment)
    }

    fun startService() = launch(loading, error) {
        appointmentRepo.startServiceAppointment(formStartService)
        success.post(R.string.success_start_service)
    }

    fun remind(id: Int) = launch(loading, error) {
        remindAppointmentRepo(id)
        success.post(R.string.success_remind_customer)
    }

    fun findFilterFormByType(
        type: Int
    ): AppointmentFilterForm {
        return if (type == 1)
            filterCheckInForm
        else filterCustomerForm
    }

    fun getKeyword(
        type: Int
    ): String {
        return if (type == 1) {
            filterCheckInForm.keyword
        } else {
            filterCustomerForm.keyword
        }
    }

    fun loadCustomer(keyword: String, page: Int) = launch(if (page == 1) loading else null, error) {
        customerRepo.search(keyword, page)
    }

    fun loadStaff(keyword: String, page: Int) = launch(if (page == 1) loading else null, error) {
        staffRepo(keyword, page)
    }
}





