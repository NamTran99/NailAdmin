package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IService {
    val id: Int get() = 0
    val name: String get() = ""
    val price: String get() = ""
}

class ServiceImpl : IService, ISelector {
    override var isSelector: Boolean = false
}

@Parcelize
class ServiceForm(
    override var id: Int = 0,
    override var name: String = "",
    override var price: String = ""
) : IService, Parcelable {

    fun validate() {
        if (name.isBlank()) resourceError(R.string.error_blank_service_name)
        if (price.isBlank()) resourceError(R.string.error_blank_service_price)
    }
}

