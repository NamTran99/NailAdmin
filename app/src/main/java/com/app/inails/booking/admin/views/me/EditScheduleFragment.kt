package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentUpdateScheduleBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.model.ui.SalonForm
import com.app.inails.booking.admin.views.me.adapters.SalonEditScheduleAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EditScheduleFragment : BaseFragment(R.layout.fragment_update_schedule), TopBarOwner {
    val viewModel by viewModel<EditScheduleVM>()
    val binding by viewBinding(FragmentUpdateScheduleBinding::bind)
    private lateinit var adapter: SalonEditScheduleAdapter

    private val schedules: List<ISchedule> = listOf(
        ISchedule(1),
        ISchedule(2),
        ISchedule(3),
        ISchedule(4),
        ISchedule(5),
        ISchedule(6),
        ISchedule(0),
    )

    companion object {
        const val REQUEST_KEY = "EditScheduleFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_business_hour,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            adapter = SalonEditScheduleAdapter(rvSchedule).apply {
                submit(schedules)
            }
            btSave.onClick {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf("schedules" to adapter.items?.toList())
                )
                activity?.onBackPressed()
            }
            val calendar = Calendar.getInstance(
                TimeZone.getTimeZone("GMT"),
                Locale.getDefault()
            )
            val currentLocalTime = calendar.time
            val date: DateFormat = SimpleDateFormat("z", Locale.getDefault())
            val localTime: String = date.format(currentLocalTime)
            tvTimeZone.text = localTime
        }

        with(viewModel) {
            salonDetail.bind {
                adapter.updateItem(it.schedules)
            }
        }

        refreshView()
    }

    private fun refreshView() {
        viewModel.getDetailSalon()
    }
}

class EditScheduleVM(
    private val profileRepository: ProfileRepository,
    private val updateSalonRepository: UpdateSalonRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val salonDetail = profileRepository.result
    private val salonForm = SalonForm()

    fun getDetailSalon() = launch(loading, error) {
        profileRepository()
    }

    fun updateSalon() = launch(loading, error) {
        updateSalonRepository(salonForm)
    }
}