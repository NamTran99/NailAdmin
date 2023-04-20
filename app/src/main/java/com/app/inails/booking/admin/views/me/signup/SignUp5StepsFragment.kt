package com.app.inails.booking.admin.views.me.signup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.core.view.viewBinding
import android.support.viewmodel.shareViewModel
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUp5StepsBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.me.signup.adapters.AllStepPagerAdapter
import com.app.inails.booking.admin.views.me.signup.adapters.StepReviewAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class SignUp5StepFragment : BaseFragment(R.layout.fragment_sign_up_5_steps), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by shareViewModel<SignUpVM>()
    val binding by viewBinding(FragmentSignUp5StepsBinding::bind)
    lateinit var stepHolderAdapter: AllStepPagerAdapter
    lateinit var stepReviewAdapter: StepReviewAdapter
    val signUpForm: SignUpForm
        get() = viewModel.salonForm

    var currentStep: Int = 1
        set(value) {
            field = value
            stepReviewAdapter.setCurrentStep(value)
            binding.vpStepSignUp.currentItem = value -1
            binding.rvAllStep.smoothScrollToPosition(value - 1)
        }

    @SuppressLint("StringFormatInvalid")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            stepHolderAdapter = AllStepPagerAdapter(this@SignUp5StepFragment)
            vpStepSignUp.apply {
                offscreenPageLimit = 5
                stepHolderAdapter = AllStepPagerAdapter(this@SignUp5StepFragment)
                adapter = stepHolderAdapter
                isUserInputEnabled = false
            }
            stepReviewAdapter = StepReviewAdapter(rvAllStep).apply {
                setCurrentStep(1)
            }
            vpStepSignUp.currentItem = currentStep - 1
            stepReviewAdapter.setCurrentStep(currentStep)
        }

        parentFragmentManager.setFragmentResultListener(
            EditScheduleFragment.REQUEST_KEY, viewLifecycleOwner
        ) { requestKey, result ->
            val listSchedule = result.getSerializable("schedules") as List<ISchedule>
            listSchedule.forEach {
                it.startTimeFormat = it.startTime.toTimeDisplay()
                it.endTimeFormat = it.endTime.toTimeDisplay()
                it.timeFormat = formatSalonSchedule(requireContext(), it)
            }
            signUpForm.schedules = listSchedule.toMutableList()
        }
        viewModel.apply {
            nextStep.bind{
                currentStep+=1
            }
            backStep.bind{
                currentStep-=1
            }
        }
    }
    private fun formatSalonSchedule(context: Context, it: ISchedule): String {
        return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) context.getString(
            R.string.not_open
        )
        else "${it.startTimeFormat} - ${it.endTimeFormat}"
    }
}
