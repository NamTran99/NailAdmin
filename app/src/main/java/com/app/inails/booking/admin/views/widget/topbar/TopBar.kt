package com.app.inails.booking.admin.views.widget.topbar

import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.TopBarMainBinding
import com.app.inails.booking.admin.databinding.TopBarSimpleBinding

class SimpleTopBarState(
    @StringRes
    private val title: Int,
    @DrawableRes
    private val iconBack: Int = R.drawable.ic_ab_back,
    private val width : Int = LinearLayout.LayoutParams.WRAP_CONTENT,
    private val onBackClick: () -> Unit = {}
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarSimpleBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btBack.setImageResource(iconBack)
            btBack.setOnClickListener { onBackClick() }
            tvTitle.layoutParams.width = width
        }
    }
}

class MainTopBarState(
    @StringRes
    private val title: Int,
    private val onMenuClick: () -> Unit = {}
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarMainBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btMenu.setOnClickListener { onMenuClick() }
        }
    }
}
