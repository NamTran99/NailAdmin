package com.app.inails.booking.admin.views.manage_staff

import android.os.Bundle
import android.support.core.view.viewBinding
import android.view.View
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentBookingBinding
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageStaffFragment : BaseRefreshFragment(R.layout.fragment_manage_staff) , TopBarOwner {
    private val binding by viewBinding(FragmentBookingBinding::bind)
    override fun onRefreshListener() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}

