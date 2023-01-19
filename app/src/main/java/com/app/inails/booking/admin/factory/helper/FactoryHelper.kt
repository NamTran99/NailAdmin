package com.app.inails.booking.admin.factory.helper

import com.app.inails.booking.admin.formatter.TextFormatter

open class FactoryHelper(val textFormatter: TextFormatter) {

    fun displaySafe(text: String?): String{
        return textFormatter.displaySafe(text)
    }

}