package com.app.inails.booking.admin.views.report

import android.os.Bundle
import android.view.View
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.views.management.service.CreateUpdateServiceOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ReportFragment:  BaseRefreshFragment(R.layout.fragment_manage_service), TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner {
    override fun onRefreshListener() {
//        viewModel.search()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBar.setState(
            SimpleTopBarState(
                R.string.mn_report
            ) { activity?.onBackPressed() })

    }
}

class ReportViewModel(){

}