package com.app.inails.booking.admin.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ListPopupWindow
import androidx.annotation.StyleRes
import androidx.viewbinding.ViewBinding
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.setMargins

abstract class BaseDialog : Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, @StyleRes theme: Int) : super(context, theme)

    var isSlideShow: Boolean = false
        set(value) {
            field = value
            if (value) window!!.attributes.windowAnimations = R.style.AppTheme_DialogAnimation
        }

    fun <T : ViewBinding> viewBinding(
        function: (context: LayoutInflater) -> T,
        isAttach: Boolean = true
    ): T {
        val binding = function(layoutInflater)
        val container = FrameLayout(context)
        setContentView(container)
        container.setMargins(R.dimen.size_20)
        container.setBackgroundResource(R.drawable.bg_white_radius)
        container.addView(binding.root)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ListPopupWindow.MATCH_PARENT, ListPopupWindow.WRAP_CONTENT)
        requestTransparent()
    }

    private fun requestTransparent() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun removeDim() {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}