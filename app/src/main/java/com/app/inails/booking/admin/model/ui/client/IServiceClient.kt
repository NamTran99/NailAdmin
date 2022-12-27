package com.app.inails.booking.admin.model.ui.client

import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.IService

interface IServiceClient
{
    val id: Int get() = 0
    val name: String get() = ""
    val price: String get() = ""
    val images: List<String>? get() = listOf()
    val priceF: Float get() = 0f
}

class ServiceImpl : IServiceClient, ISelector {
    override var isSelector: Boolean = false
}