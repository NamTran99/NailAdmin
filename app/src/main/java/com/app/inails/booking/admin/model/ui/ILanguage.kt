package com.app.inails.booking.admin.model.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.support.ISelector
import com.google.android.youtube.player.internal.u

data class Language(
    @StringRes val name: Int = 0,
    @DrawableRes val flag: Int = 0,
    val code: String,
    override var isSelector: Boolean = false,
) : ISelector

fun getListDefaultLanguage(currentLanguage: String) = listOf<Language>(
    Language(R.string.vietnamese, R.drawable.ic_vietnam, "vi"),
    Language(R.string.english, R.drawable.ic_englan,"en")
).map {
    it.isSelector = it.code == currentLanguage
    it
}

fun getListDefaultLanguage1(currentLanguage: String) = listOf<Language>(
    Language(R.string.vietnamese1, R.drawable.ic_vietnam, "vi"),
    Language(R.string.english1, R.drawable.ic_englan,"en")
).map {
    it.isSelector = it.code == currentLanguage
    it
}

