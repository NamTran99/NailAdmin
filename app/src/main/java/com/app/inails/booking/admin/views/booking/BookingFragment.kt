package com.app.inails.booking.admin.views.booking

import android.os.Bundle
import android.support.core.view.viewBinding
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentBookingBinding
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.ViewPager2Adapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.tabs.TabLayout

class BookingFragment : BaseFragment(R.layout.fragment_booking), TopBarOwner {
    private val binding by viewBinding(FragmentBookingBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btAddAppointment.setOnClickListener {
            Router.open(this, Routing.CreateAppointment)
        }

        Adapter(binding.appointViewPager, this).apply {
            submit(DataConst.AppointmentTab.appointment)
            setupPageChangeWith(binding.appointTab)
        }

        appEvent.changeTabBooking.bind {
            changeTab(tab = it)
        }
    }

    class Adapter(viewPager: ViewPager2, private val fragment: Fragment) :
        ViewPager2Adapter(viewPager, fragment) {

        override fun getItemCount(): Int {
            return mRoute?.size ?: 0
        }

        override fun createFragment(position: Int): Fragment {
            return mRoute?.get(position)?.second ?: AppointmentFragment(1)
        }

        override fun onBindTab(position: Int, tab: TabLayout.Tab) {
            tab.text = fragment.getText(mRoute?.get(position)?.first ?: 0)
        }
    }

    fun changeTab(tab: Int = 0) {
        binding.run {
            appointViewPager.postDelayed(
                Runnable {
                    appointViewPager.currentItem = tab
                }, 10
            )
        }
    }
}





