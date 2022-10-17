package com.app.inails.booking.admin.views.me

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.view.viewBinding
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentUpdateScheduleBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.me.adapters.SalonEditScheduleAdapter
import com.app.inails.booking.admin.views.widget.topbar.ExtensionButton
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class EditScheduleArgs(
    val schedules: List<ISchedule> = listOf(),
    val timeZoneDisplay: String = ""
) : BundleArgument

class EditScheduleFragment : BaseFragment(R.layout.fragment_update_schedule), TopBarOwner {
    val binding by viewBinding(FragmentUpdateScheduleBinding::bind)
    private lateinit var adapter: SalonEditScheduleAdapter
    private val arg by lazy { BundleArgument.of(arguments) ?: EditScheduleArgs() }

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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_business_hour,
                onBackClick = {
                    activity?.onBackPressed()
                },
                extensionButton = ExtensionButton(
                    isShow = true,
                    content = "Save",
                    onclick = {
                        val schedule = adapter.items?.find {
                            (it.startTime != null && it.endTime == null) ||
                                    (it.endTime != null && it.startTime == null)
                        }
                        if (schedule != null) {
                            if (schedule.startTime == null) {
                                toast("Please select the Start Time of ${schedule.dayFormat}")
                            }

                            if (schedule.endTime == null) {
                                toast("Please select the End Time of ${schedule.dayFormat}")
                            }

                            return@ExtensionButton
                        }
                        parentFragmentManager.setFragmentResult(
                            REQUEST_KEY,
                            bundleOf("schedules" to adapter.items?.toList())
                        )
                        activity?.onBackPressed()
                    }
                )
            )
        )

        with(binding) {
            adapter = SalonEditScheduleAdapter(rvSchedule).apply {
                submit(schedules)
                updateItem(arg.schedules)
            }
            tvTimeZone.setText(arg.timeZoneDisplay)
        }
    }
}