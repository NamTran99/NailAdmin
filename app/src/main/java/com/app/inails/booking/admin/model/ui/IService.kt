package com.app.inails.booking.admin.model.ui

import com.app.inails.booking.model.support.ISelector

interface IService {
    val id: Int get() = 0
    val name: String get() = ""
}

class ServiceImpl : IService, ISelector {
    override var isSelector: Boolean = false
}