package com.app.inails.booking.admin.model.response

import com.google.gson.annotations.SerializedName

data class SupportSignUpDTO(
    var type: String = "salon",
    var name: String = "",
    var phone: String = "",
    var password: String = "",

    @SerializedName("salon_name")
    var salonName: String = "",
    @SerializedName("salon_email")
    var salonEmail: String = "",
    var salonPhone: String = "",
    var salonState: String = "",
)

