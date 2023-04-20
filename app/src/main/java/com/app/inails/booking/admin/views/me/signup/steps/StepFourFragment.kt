package com.app.inails.booking.admin.views.me.signup.steps

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.core.livedata.forceRefresh
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
import android.support.viewmodel.shareViewModel
import android.support.viewmodel.viewModel
import android.view.View
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUpAccountBinding
import com.app.inails.booking.admin.databinding.FragmentStepFourBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.toTimeDisplay
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.model.ui.SignUpForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToEditScheduleSalon
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.EditScheduleArgs
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.adapters.ChooseOneForAllDateDialogOwner
import com.app.inails.booking.admin.views.me.adapters.SalonEditScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.me.signup.SignUpVM
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlin.math.sign

class StepFourFragment : BaseFragment(R.layout.fragment_step_four), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner, ChooseOneForAllDateDialogOwner {
    val viewModel by shareViewModel<SignUpVM>()
    val binding by viewBinding(FragmentStepFourBinding::bind)
    val signUpForm: SignUpForm
        get() = viewModel.salonForm
    private lateinit var adapter: SalonEditScheduleAdapter
//    private lateinit var scheduleAdapter: SalonScheduleAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up,
                onBackClick = {
                    confirmDialog.show(
                        title = getString(R.string.notice),
                        message = getString(R.string.message_exit),
                        function = {
                            activity?.onBackPressed()
                        }
                    )
                },
            )
        )

        binding.apply {
//            scheduleAdapter = SalonScheduleAdapter(rcvSchedule).apply {
//                submit(signUpForm.schedules)
//            }
            btSkip.onClick{
                viewModel.nextStep.forceRefresh()
            }
            val edit = EditScheduleFragment()
            adapter = SalonEditScheduleAdapter(rvSchedule).apply {
                submit(ISchedule.getDefaultList())
                onItemCopyClick = {
                    chooseOneForAllDateDialog.show(it,adapter.items!!.toList())
                }
            }
            chooseOneForAllDateDialog.onSaveClick = { list ->
                adapter.updateItem(list)
            }
//            tvTimeZone.text = arg.timeZoneDisplay
//            tvEditSchedule.onClick {
//                val editScheduleArgs = EditScheduleArgs(
//                    signUpForm.schedules,
//                    "${signUpForm.zoneID} UTC ${signUpForm.offsetDisplay}"
//                )
//                Router.run {
//                    redirectToEditScheduleSalon(editScheduleArgs) }
//            }
            btnBack.onClick{
                viewModel.backStep.forceRefresh()
            }
        }

        viewModel.apply {
            timeZoneResult.bind {
                val oldTimezone = salonForm.getTimeZoneDisplay(requireContext(), false)
                val newTimeZone = salonForm.getTimeZoneDisplay(requireContext(), false)
               binding.tvTimeZone.text = salonForm.getTimeZoneDisplay(requireContext(), false)

                if (oldTimezone != newTimeZone) {
                    messageDialog.show(
                        R.string.notice,
                        getString(R.string.change_time_zone_2, newTimeZone)
                    )
                }
            }
        }
        binding.btnContinue.onClick {
            val schedule = adapter.items?.find {
                (it.startTime != null && it.endTime == null) ||
                        (it.endTime != null && it.startTime == null)
            }
            if (schedule != null) {
                if (schedule.startTime == null) {
                    toast(requireContext().getString(R.string.error_blank_start_time, requireContext().getString(schedule.dayFormat)))
                }

                if (schedule.endTime == null) {
                    toast(requireContext().getString(R.string.error_blank_end_time, requireContext().getString(schedule.dayFormat)))
                }

                return@onClick
            }
            signUpForm.schedules = adapter.items!!
            viewModel.nextStep.forceRefresh()
        }


    }
}
