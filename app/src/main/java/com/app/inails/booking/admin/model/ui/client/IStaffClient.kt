package com.app.inails.booking.admin.model.ui.client

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.IStaff

interface IStaffClient {
    val id: Int get() = 0
    val name: String get() = ""
    val status: Pair<Int, Int> get() = R.string.status_staff_available to R.drawable.ic_oval_green
    val isShowStatus: Boolean get() = true
    val avatar : String get() = ""
}

class IStaffImpl : IStaffClient, ISelector {
    override var isSelector: Boolean = false
}