package com.app.inails.booking.admin.extention

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.utils.FileUtils

fun Context.getFilePath(it: Uri) = FileUtils.getPath(this, it)

fun BaseFragment.copyText(text: String) {
    val clipboard: ClipboardManager? =
        this.requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("nails:text:copy", text)
    clipboard?.setPrimaryClip(clip)
    this.success("Copied!")
}