package com.app.inails.booking.admin.views.booking.detail

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
import com.app.inails.booking.admin.databinding.FragmentAppointmentDetailBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.booking.AppointmentDetailRepository
import com.app.inails.booking.admin.repository.booking.AppointmentRepository
import com.app.inails.booking.admin.views.booking.*
import com.app.inails.booking.admin.views.booking.dialog.VoucherDetailDialogOwner
import com.app.inails.booking.admin.views.extension.LocalImage
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs1
import com.app.inails.booking.admin.views.base.AppImagesAdapter
import com.app.inails.booking.admin.views.widget.topbar.ExtensionButton
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AppointmentDetailFragment : BaseFragment(R.layout.fragment_appointment_detail),
    TopBarOwner, AcceptAppointmentOwner, RejectAppointmentOwner,
    StartServicesOwner, FinishBookingOwner, CustomerInfoOwner, StaffInfoDialogOwner,
    VoucherDetailDialogOwner {
    private val binding by viewBinding(FragmentAppointmentDetailBinding::bind)
    private val viewModel by viewModel<AppointmentDetailViewModel>()
    private val arg by lazy { argument<Routing.AppointmentDetail>() }
    private var mAppointment: IAppointment? = null

    private var beforeImagePath = ArrayList<AppImage>()
    private var afterImagePath = ArrayList<AppImage>()

    private lateinit var mFeedbackImageAdapter: AppImagesAdapter
    private lateinit var mBeforeImagesAdapter: AppImagesAdapter
    private lateinit var mAfterImagesAdapter: AppImagesAdapter
//    private lateinit var mMoreServiceAdapter: ServicePriceAdapter

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
        finishBookingDialog.apply {
            onAddBeforeImage = {
                FishBun.with(this@AppointmentDetailFragment)
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
                FishBun.with(this@AppointmentDetailFragment)
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
            onSearchService = {
                viewModel.searchService(it)
            }
        }
        topBar.setState(
            SimpleTopBarState(
                R.string.title_appointment_detail,
                extensionButton = ExtensionButton(isShow = false, onclick = {
                    Router.redirectToCreateAppointment(self, arg.id)
                }),
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { viewModel.refresh(arg.id) }
//            mMoreServiceAdapter = ServicePriceAdapter(rvMoreServices, isShowBottom = true)
            mFeedbackImageAdapter = AppImagesAdapter(rcFeedbackImage).apply {
                onItemImageClick = {
                    Router.run {
                        redirectToShowZoomImage1(
                            ShowZoomImageArgs1(
                                this@apply.items().getData().map { LocalImage(it.image) }, it
                            )
                        )
                    }
                }
            }
            mBeforeImagesAdapter = AppImagesAdapter(rcBeforePhoto).apply {
                onItemImageClick = {
                    Router.run {
                        redirectToShowZoomImage1(
                            ShowZoomImageArgs1(
                                this@apply.items().getData().map { LocalImage(it.image) }, it
                            )
                        )
                    }
                }
            }
            mAfterImagesAdapter = AppImagesAdapter(rcAfterphoto).apply {
                onItemImageClick = {
                    Router.run {
                        redirectToShowZoomImage1(
                            ShowZoomImageArgs1(
                                this@apply.items().getData().map { LocalImage(it.image) }, it
                            )
                        )
                    }
                }
            }
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
            serviceLoadingResult.bind {
                finishBookingDialog.updateServiceSearch(it)
            }

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
                success(R.string.client_check_in_success)
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
        mFeedbackImageAdapter.clear()
        mBeforeImagesAdapter.clear()
        mAfterImagesAdapter.clear()

        mAppointment = item
        with(binding) {
            btnInfo.onClick {
                item.voucher?.let {
                    voucherDetailDialog.show(it)
                }
            }
//            mMoreServiceAdapter.submit(item.serviceCustom)
            tvCode.text = item.voucherCode
            tvPercent.text = item.percentDisplay
            tvPercent.show(item.showPercent)
//            txtVoucherCode.text = item.voucherCode
//            txtDiscount.show(item.showPercent)
//            txtDiscount.text = item.percent
            txtPriceDiscount.text = item.discountDisplay
            if(item.totalAmount <= 0.0){
                tvFree.show()
                txtTotalAmount.hide()
            }else{
                tvFree.hide()
                txtTotalAmount.show()
                txtTotalAmount.text = item.totalAmountDisplay
            }
            voucherLayout.show(item.hasVoucher)
            lvBeforeAfterImg.hide(item.beforeImage.isEmpty() && item.afterImage.isEmpty())
            tvBeforeImg.hide(item.beforeImage.isEmpty())
            tvAfterImg.hide(item.afterImage.isEmpty())
            mFeedbackImageAdapter.submit(item.feedbackImages)
            mBeforeImagesAdapter.submit(item.beforeImage)
            mAfterImagesAdapter.submit(item.afterImage)
//            txtTotal.text = item.totalPriceService
            tvCustomerName.text = item.customerName
            tvTimeAndDate.text = item.dateAppointment
            tvStaffName.text = item.staffName
            tvID.text = item.id.formatID()
            tvStatus.text = item.statusDisplay
            tvStatus.drawableStart(item.resIconStatus)
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), item.colorStatus))
            tvNotes.text = item.noteFinish
            tvNotes.show(!item.noteFinish.isNullOrEmpty())
            (item.status == DataConst.AppointmentStatus.APM_CANCEL || item.status == DataConst.AppointmentStatus.APM_FINISH) show btDelete
            afterAcceptLayout.show((item.status == DataConst.AppointmentStatus.APM_WAITING || item.status == DataConst.AppointmentStatus.APM_IN_PROCESSING) && item.type == 1)
            (item.status == DataConst.AppointmentStatus.APM_ACCEPTED && item.type == 2) show acceptLayout
            (item.status == DataConst.AppointmentStatus.APM_PENDING && item.type == 2) show waitingLayout
//            (item.status == DataConst.AppointmentStatus.APM_FINISH) show finishLayout
            (item.status == DataConst.AppointmentStatus.APM_CANCEL) show cancelLayout
            val list = item.serviceListAll.toMutableList()
//            item.serviceCustomObj?.let { list.add(it) }
            // NamTD8: note
            (ServicePriceAdapter(rvServices)).apply {
                submit(list)
            }
            btService.show(item.status == DataConst.AppointmentStatus.APM_WAITING)
            btFinish.show(item.status == DataConst.AppointmentStatus.APM_IN_PROCESSING)
//            btService.drawableStart(if (item.status != DataConst.AppointmentStatus.APM_IN_PROCESSING) R.drawable.ic_uncheck_white else R.drawable.ic_check_blue )
//            btFinish.drawableStart(if (item.status != DataConst.AppointmentStatus.APM_FINISH) R.drawable.ic_uncheck_white else R.drawable.ic_check_green )

            tvTypeCancel.text = item.canceledBy
            tvReason.text = item.reasonCancel
            feedbackLayout.show(item.hasFeedback)
            tvFeedbackContent.text = item.feedbackContent
            ratingBar.rating = item.feedbackRating.toFloat()
            tvPhone.text = item.phone.formatPhoneUSCustom()
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
                    extensionButton = ExtensionButton(isShow = item.status != DataConst.AppointmentStatus.APM_FINISH && item.status != DataConst.AppointmentStatus.APM_CANCEL && item.status != DataConst.AppointmentStatus.APM_IN_PROCESSING,
                        onclick = {
                            Router.redirectToCreateAppointment(self, arg.id)
                        }
                    ),
                    onBackClick = {
                        activity?.onBackPressed()
                    },
                )
            )
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
                finishBookingDialog.show(mAppointment!!) { amount, notes, listMore ->
                    viewModel.form.run {
                        id = mAppointment!!.id
                        price = amount
                        note = notes
                        status = DataConst.AppointmentStatus.APM_FINISH
                        beforeImages = beforeImagePath
                        afterImages = afterImagePath
                        moreService = listMore
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
    private val fetchListServiceRepo: FetchListServiceRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val appointment = appointmentDetailRepo.result
    val appointmentAction = appointmentRepo.result
    val appointmentCheckIn = appointmentRepo.resultCheckIn
    val idRemove = appointmentRepo.resultRemove
    val form = AppointmentStatusForm()
    val formCancel = CancelAppointmentForm()
    val formHandle = HandleAppointmentForm()
    val formStartService = StartServiceForm()
    val success = SingleLiveEvent<Int>()
    val checkInSuccess = SingleLiveEvent<Any>()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    fun refresh(id: Int) = launch(loadingCustom, error) {
        appointmentDetailRepo(id)
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

    val searchScope = CoroutineScope(Dispatchers.Main)
    val serviceListResult = fetchListServiceRepo.results
    val serviceLoadingResult =
        serviceListResult.combine1(refreshLoading as MutableLiveData<Boolean>) { items, loading ->
            items to loading
        }

    fun searchService(keyword: String = "", page: Int = 1) =
        launch(refreshLoading, error, searchScope.coroutineContext, true) {
            fetchListServiceRepo(keyword, page)
        }
}
