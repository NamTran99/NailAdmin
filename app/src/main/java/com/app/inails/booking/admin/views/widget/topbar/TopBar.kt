package com.app.inails.booking.admin.views.widget.topbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.TopBarMainBinding
import com.app.inails.booking.admin.databinding.TopBarSimpleBinding
import com.app.inails.booking.admin.extention.show

class SimpleTopBarState(
    @StringRes
    private val title: Int,
    @DrawableRes
    private val iconBack: Int = R.drawable.ic_ab_back,
    private val showEdit: Boolean = false,
    private val onBackClick: () -> Unit = {},
    private val onEditClick: (() -> Unit)? = null,

) : TopBarState() {
    override val stateBinding by bindingOf(TopBarSimpleBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btBack.setImageResource(iconBack)
            btBack.setOnClickListener { onBackClick() }
            btEdit.show(showEdit)
            btEdit.setOnClickListener {
                onEditClick?.let { it1 -> it1() }
            }
        }
    }
}

class MainTopBarState(
    @StringRes
    private val title: Int,
    private val onMenuClick: () -> Unit = {},
    private val onStaffListClick: () -> Unit = {},
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarMainBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btMenu.setOnClickListener { onMenuClick() }
            btStaffList.setOnClickListener {
                onStaffListClick()
            }
        }
    }
}
