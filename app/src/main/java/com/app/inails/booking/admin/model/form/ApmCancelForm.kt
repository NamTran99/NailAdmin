package com.app.inails.booking.admin.model.form

import com.google.gson.annotations.SerializedName

class ApmCancelForm(
    var id: Long? = 0,
    var reason: String? = "",
    @SerializedName("canceled_by")
    val cancelBy: String = "2"//Customer
)