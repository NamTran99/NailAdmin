package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.route.nullableArguments
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentCreateAppointmentBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.extention.bind
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.extention.toServerUTC
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.AppointmentForm
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.booking.AppointmentDetailRepository
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.TimePickerDialog
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppointmentArg(
    val id: Int? = 0
) : BundleArgument

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
            etPhone.inputTypePhoneUS()
            mDatePickerDialog.setupClickWithView(tvSelectDate)
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
                    services = mServiceAdapter.selectedItems.toString()
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
            appointment.bind(::display)
        }

        appEvent.chooseStaffInCreateAppointment.observe(this) {
            if (it != null) {
                mStaff = it
                binding.tvChooseStaff.text = it.name
                viewModel.form.staffID = it.id
            }
        }

//        binding.etFullName.bind {
//            viewModel.form.name = it }
//        binding.etPhone.bind { viewModel.form.phone = it }
//        // save state
//        binding.etFullName.setText(viewModel.form.name)
//        binding.etPhone.setText(viewModel.form.phone)

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
            etFullName.setText(apm.customerName)
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
            if (apm.serviceCustomObj != null) {
                etNote.setText(apm.serviceCustomObj!!.name)
                etNote.show()
                (mServiceAdapter.items?.last() as ISelector).isSelector = true
            }
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
    val success = SingleLiveEvent<String>()
    val appointment = appointmentDetailRepo.result

    init {
        onLaunch()
    }

    private fun onLaunch() = launch(loading, error) {
        serviceRepo()
    }

    fun submit() = launch(loading, error) {
        createAppointmentRepo(form)
        success.post("Create appointment for customer success!")
    }

    fun update() = launch(loading, error) {
        createAppointmentRepo.update(form)
        success.post("Update appointment success")
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

