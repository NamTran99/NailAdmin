package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
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
import com.app.inails.booking.admin.databinding.FragmentCreateAppointmentBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.AppointmentForm
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.dialog.picker.TimePickerDialog
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppointmentArg(
    val id: Int? = 0
) : BundleArgument

class CreateAppointmentFragment : BaseFragment(R.layout.fragment_create_appointment), TopBarOwner {
    private val binding by viewBinding(FragmentCreateAppointmentBinding::bind)
    private val viewModel by viewModel<CreateAppointmentViewModel>()
    private lateinit var mServiceAdapter: SelectServiceAdapter
    private val mDatePickerDialog by lazy { DatePickerDialog(appActivity) }
    private val mTimePickerDialog by lazy { TimePickerDialog(appActivity) }
    private var mDateSelected = ""
    private var mTimeSelected = ""
    private var mDateTagSelected = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_create_new_appointment
            ) { activity?.onBackPressed() })
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
                    serviceCustom = etSomethingElse.text.toString()
                    workTime = workingTime
                    services = mServiceAdapter.selectedItems.toString()
                    dateAppointment = if (tvSelectTime.text.toString()
                            .isEmpty()
                    ) "" else "${tvSelectDate.tag} ${tvSelectTime.text}"
                }
                viewModel.submit()
            }

        }

        with(viewModel) {
            services.bind(mServiceAdapter.apply {
                onClickItemListener = {
                    viewModel.form.hasServiceCustom = it
                    (it) show binding.etSomethingElse
                    if (!it) binding.etSomethingElse.setText("")
                }
            }::submit)
            success.bind {
                success("Create appointment for customer success!")
                activity?.onBackPressed()
            }
        }

        appEvent.chooseStaffInCreateAppointment.observe(this) {
            if (it != null) {
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
    }

    override fun onResume() {
        super.onResume()
        if (mDateSelected.isNotEmpty()) {
            binding.tvSelectDate.text = mDateSelected
            binding.tvSelectDate.tag = mDateTagSelected
        }
        if (mTimeSelected.isNotEmpty()) binding.tvSelectTime.text = mTimeSelected
        (viewModel.form.hasServiceCustom) show binding.etSomethingElse
    }

}


class CreateAppointmentViewModel(
    private val serviceRepo: ServiceRepository,
    private val createAppointmentRepo: CreateAppointmentRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val services = serviceRepo.results
    val form = AppointmentForm()
    val success = SingleLiveEvent<Any>()

    init {
        onLaunch()
    }

    private fun onLaunch() = launch(loading, error) {
        serviceRepo()
    }

    fun submit() = launch(loading, error) {
        success.post(createAppointmentRepo(form))
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
        var servicesList = bookingApi.services(userLocalSource.getSalonID().toString())
            .await()
        servicesList = servicesList
        servicesList.add(ServiceDTO(name = "Something Else"))
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
}

