package com.app.inails.booking.admin.model.form

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.google.gson.annotations.SerializedName

class VoucherForm(
    var code: String = "",
    @SerializedName("salon_id")
    var salonId: Long = 0

    ) {


    fun validate() {
        if (code.isEmpty()) resourceError(R.string.error_blank_voucher_code)
    }
}