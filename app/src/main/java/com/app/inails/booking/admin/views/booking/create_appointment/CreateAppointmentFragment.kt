package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentCreateAppointmentBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.functional.UsPhoneNumberFormatter
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize
import java.lang.ref.WeakReference

@Parcelize
data class AppointmentArg(
    val id: Int? = 0
) : BundleArgument

class CreateAppointmentFragment : BaseFragment(R.layout.fragment_create_appointment), TopBarOwner {
    private val binding by viewBinding(FragmentCreateAppointmentBinding::bind)
    private val viewModel by viewModel<CreateAppointmentViewModel>()
    private lateinit var mServiceAdapter: SelectServiceAdapter
    private val mDatePickerDialog by lazy { DatePickerDialog(appActivity) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_create_new_appointment
            ) { activity?.onBackPressed() })
        mServiceAdapter = SelectServiceAdapter(binding.rvServices)

        with(binding) {
            val addLineNumberFormatter = UsPhoneNumberFormatter(WeakReference(etPhone))
            etPhone.run {
                addTextChangedListener(addLineNumberFormatter)
            }
            swStaff.setOnCheckedChangeListener { buttonView, isChecked ->
                (!isChecked) show binding.tvChooseStaff
            }
            mDatePickerDialog.setupClickWithView(tvSelectDate)
        }

        with(viewModel) {
            services.bind(mServiceAdapter.apply {
                onClickItemListener = {
                    (it) show binding.etSomethingElse
                }
            }::submit)
        }


    }

}


class CreateAppointmentViewModel(
    private val serviceRepo: ServiceRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val services = serviceRepo.results

    init {
        onLaunch()
    }

    private fun onLaunch() = launch(loading, error) {
        serviceRepo()
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
        servicesList = servicesList.toMutableList()
        servicesList.add(ServiceDTO(name = "Something Else"))
        results.post(
            bookingFactory
                .createServiceList(
                    servicesList
                )
        )
    }
}

