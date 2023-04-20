package com.app.inails.booking.admin.model.ui

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.ColorRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.support.ISelector
import kotlinx.parcelize.Parcelize

interface IService{
    val id: Int get() = 0
    val name: String get() = ""
    val price: Double get() = 0.0
    val isActive: Int get() = 0
    val detailImages: List<AppImage> get() = listOf()
    val avatar: String? get() = null
    val moreImage:  List<AppImage> get() = listOf()
    val textColor: Int @ColorRes get() = R.color.white
    val serviceCustom: List<IService> get() = listOf()
}

class MoreServiceForm(
    override var name: String = "",
    override val price: Double = 0.0
): IService

@Parcelize
class ServiceImpl : IService, ISelector, Parcelable {
    override var isSelector: Boolean = false
}

class ServiceForm(
    override var id: Int = 0,
    override var name: String = "",
    override var price: Double = 0.0,
    override var avatar: String? = null,
    override var moreImage: List<AppImage> = listOf(),
    var oldServerImages : List<AppImage> = listOf(),
    @Transient
    var price_input: String = "",
) : IService {

    @SuppressLint("SuspiciousIndentation")
    fun validate() {
        if (avatar == null) resourceError(R.string.error_blank_service_avatar)
        if (name.isBlank()) resourceError(R.string.error_blank_service_name)
        if (price_input.isBlank()) resourceError(R.string.error_blank_service_price)
            price = price_input.toDoubleOrNull().safe()
    }
}

