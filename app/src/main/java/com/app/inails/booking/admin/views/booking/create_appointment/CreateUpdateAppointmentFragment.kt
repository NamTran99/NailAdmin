package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.nullableArguments
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
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentCreateAppointmentBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToApmList
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToFindCustomer
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.booking.AppointmentDetailRepository
import com.app.inails.booking.admin.views.booking.create_appointment.adapter.SelectServiceAdapter
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.TimePickerDialog
import com.app.inails.booking.admin.views.widget.setEnable
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class CreateUpdateAppointmentFragment : BaseFragment(R.layout.fragment_create_appointment),
    TopBarOwner {
    private val binding by viewBinding(FragmentCreateAppointmentBinding::bind)
    private val viewModel by viewModel<CreateAppointmentViewModel>()
    private val arg by lazy { nullableArguments<Routing.CreateAppointment>() }
    private lateinit var mServiceAdapter: SelectServiceAdapter
    private val mDatePickerDialog by lazy { DatePickerDialog(appActivity) }
    private val mTimePickerDialog by lazy { TimePickerDialog(appActivity) }
    private var mDateSelected = ""
    private var mTimeSelected = ""
    private var mDateTagSelected = ""
    private var loadData = false
    private var mStaff: IStaff? = null
    private var mHour = 0
    private var mMinute = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                if (arg?.id != null) R.string.title_update_appointment else R.string.title_create_new_appointment,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )
        mServiceAdapter = SelectServiceAdapter(binding.rvServices)
        with(binding) {
            etPhone.onClick {
                redirectToFindCustomer(self)
            }

            etFullName.onClick {
                redirectToFindCustomer(self)
            }
            appEvent.findingCustomer.bind {
                viewModel.form.customer_id = it.first.id
                etPhone.setText(it.first.phone)
                etFullName.setTextCustom(it.first.name)
                if(it.second){
                    etPhone.setEnable(false)
                    etFullName.setEnable(false)
                }
            }
            mDatePickerDialog.setupClickWithView(tvSelectDate, true)
            mTimePickerDialog.setupClickWithView(tvSelectTime)
            tvChooseStaff.setOnClickListener {
                Router.run { redirectToChooseStaff() }
            }

            swStaff.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.form.hasStaff = !isChecked
                (!isChecked) show binding.tvChooseStaff
            }

            btAddAppointment.setOnClickListener {
                val hours = (spHour.selectedItem as String).toInt()
                val minutes = (spMinute.selectedItem as String).toInt()
                val workingTime = (hours * 60) + minutes
                viewModel.form.run {
                    phone = etPhone.text.toString()
                    name = etFullName.text.toString()
                    note = etNote.text.toString()
                    workTime = workingTime
                    services = if (mServiceAdapter.selectedItems.isNotEmpty()) {
                        mServiceAdapter.selectedItems.toString()
                    } else
                        ""
                    dateAppointment = if (tvSelectTime.text.toString()
                            .isEmpty()
                    ) "" else "${tvSelectDate.tag} ${tvSelectTime.text}".toServerUTC()
                }
                if (arg?.id != null)
                    viewModel.update()
                else
                    viewModel.submit()
            }
            btAddAppointment.setText(if (arg?.id == null) R.string.btn_add_appointment else R.string.btn_update)
        }

        with(viewModel) {
            services.bind {
                if (arg?.id != null && arg?.id != 0 && !loadData) {
                    loadData = true
                    viewModel.detail(arg!!.id!!)
                }
            }
            services.bind(mServiceAdapter::submit)
            success.bind {
                success(it)
                activity?.onBackPressed()
            }
            createApmSuccess.bind {
                success(it)
                redirectToApmList()
            }
            appointment.bind(::display)
        }

        appEvent.chooseStaffInCreateAppointment.observe(this) {
            if (it != null) {
                mStaff = it
                binding.tvChooseStaff.text = it.name
                viewModel.form.staffID = it.id
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mDateSelected = binding.tvSelectDate.text.toString()
        mTimeSelected = binding.tvSelectTime.text.toString()
        mDateTagSelected = binding.tvSelectDate.tag.toString()
        mHour = binding.spHour.selectedItemPosition
        mMinute = binding.spMinute.selectedItemPosition
    }

    override fun onResume() {
        super.onResume()
        if (mDateSelected.isNotEmpty()) {
            binding.tvSelectDate.text = mDateSelected
            binding.tvSelectDate.tag = mDateTagSelected
        }
        if (mTimeSelected.isNotEmpty()) binding.tvSelectTime.text = mTimeSelected

        binding.spMinute.setSelection(mMinute)
        binding.spHour.setSelection(mHour)
    }

    private fun display(apm: IAppointment) {
        with(binding) {
            viewModel.form.id = apm.id
            etPhone.setText(apm.phone)
            etFullName.setTextCustom(apm.customerName)
            if (apm.staffID > 0 && mStaff == null) {
                viewModel.form.staffID = apm.staffID
                tvChooseStaff.text = apm.staffName
            }
            tvSelectDate.text = apm.dateSelected
            tvSelectDate.tag = apm.dateTag
            tvSelectTime.text = apm.timeSelected

            val minute = apm.workTime % 60 / 10
            val hour = apm.workTime / 60
            spHour.setSelection(hour)
            spMinute.setSelection(minute)
            etNote.setText(apm.somethingElse)
            etNote.show()
            (mServiceAdapter.items?.last() as ISelector).isSelector = true
            mServiceAdapter.setSelected(apm.serviceList)
        }
    }

}

class CreateAppointmentViewModel(
    private val serviceRepo: ServiceRepository,
    private val createAppointmentRepo: CreateAppointmentRepository,
    private val appointmentDetailRepo: AppointmentDetailRepository,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val services = serviceRepo.results
    val form = AppointmentForm()
    val createApmSuccess = SingleLiveEvent<Int>()
    val success = SingleLiveEvent<Int>()
    val appointment = appointmentDetailRepo.result

    init {
        onLaunch()
    }

    private fun onLaunch() = launch(loading, error) {
        serviceRepo()
    }

    fun submit() = launch(loading, error) {
        createAppointmentRepo(form)
        createApmSuccess.post(R.string.success_create_appointment)
    }

    fun update() = launch(loading, error) {
        createAppointmentRepo.update(form)
        success.post(R.string.success_update_appointment)
    }

    fun detail(id: Int) = launch(loading, error) {
        appointmentDetailRepo(id)
    }
}


@Inject(ShareScope.Fragment)
class ServiceRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<IService>>()
    suspend operator fun invoke() {
        val servicesList = bookingApi.services(userLocalSource.getSalonID().toString())
            .await()
        results.post(
            bookingFactory
                .createServiceList(
                    servicesList
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class CreateAppointmentRepository(
    private val bookingApi: BookingApi,
    private val textFormatter: TextFormatter
) {
    suspend operator fun invoke(form: AppointmentForm) {
        form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        bookingApi.createAppointment(form).await()
    }

    suspend fun update(form: AppointmentForm) {
        form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        bookingApi.updateAppointment(form).await()
    }
}

