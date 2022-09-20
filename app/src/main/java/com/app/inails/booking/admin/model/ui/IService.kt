package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IService{
    val id: Int get() = 0
    val name: String get() = ""
    val price: Double get() = 0.0
    val isActive: Int get() = 0
    val textColor: Int @ColorRes get() = R.color.white
}

class ServiceImpl : IService, ISelector {
    override var isSelector: Boolean = false
}

@Parcelize
class ServiceForm(
    override var id: Int = 0,
    override var name: String = "",
    override var price: Double = 0.0,
    @Transient
    var price_input: String = ""
) : IService, Parcelable {

    fun validate() {
        if (name.isBlank()) resourceError(R.string.error_blank_service_name)
        if (price_input.isBlank()) resourceError(R.string.error_blank_service_price)
            price = price_input.toDoubleOrNull().safe()
    }
}

