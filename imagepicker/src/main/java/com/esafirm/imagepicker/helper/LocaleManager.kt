package com.esafirm.imagepicker.helper

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.Locale

object LocaleManager {

    var mLanguage: String = Locale.getDefault().language

    fun updateResources(context: Context, language: String? = null): Context {
        var resultContext = context
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        val localeToSwitchTo = Locale(language?: mLanguage)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else {
            configuration.locale = localeToSwitchTo
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            resultContext = context.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        return resultContext
    }
}
