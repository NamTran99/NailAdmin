package com.app.inails.booking.admin.model.ui

import android.os.Bundle
import android.os.Parcelable
import com.app.inails.booking.model.support.ISelector
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IStaff {
    val id: Int get() = 0
    val name: String get() = ""
    val phone: String get() = ""
}

@Parcelize
class StaffForm(
    override var id: Int = 0,
    override var phone: String = "",
    override var name: String = ""
) : IStaff, Parcelable{
    fun toBundle(): Bundle{
        val bundle = Bundle()
        bundle.putParcelable("staff", this)
        return bundle
    }
}


class StaffImpl : IStaff, ISelector {
    override var isSelector: Boolean = false
}