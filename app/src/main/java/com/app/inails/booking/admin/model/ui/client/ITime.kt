package com.app.inails.booking.admin.model.ui.client

import com.app.inails.booking.admin.model.support.ISelector

interface ITime {
    val id: Int get() = 0
    val time: String get() = ""
    val timeDisplay: String get() = ""
}

class ITimeImpl : ITime, ISelector {
    override var isSelector: Boolean = false
}