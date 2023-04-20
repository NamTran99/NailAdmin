package com.app.inails.booking.admin.views.me.signup.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.inails.booking.admin.views.me.signup.steps.*


class AllStepPagerAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = COUNT_ALL
    override fun createFragment(position: Int): Fragment = when(position){
        POSITION_0 -> StepOneFragment()
        POSITION_1 -> StepTwoFragment()
        POSITION_2 -> StepThreeFragment()
        POSITION_3 -> StepFourFragment()
        POSITION_4 -> StepFiveFragment()
        else -> StepOneFragment()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    companion object{
        const val COUNT_ALL = 5
        const val POSITION_0 = 0
        const val POSITION_1 = 1
        const val POSITION_2 = 2
        const val POSITION_3 = 3
        const val POSITION_4 = 4
    }
}