package com.app.inails.booking.admin.views.widget.topbar

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.TopBarMainBinding
import com.app.inails.booking.admin.databinding.TopBarSimpleBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show

class SimpleTopBarState(
    @StringRes
    private val title: Int,
    @DrawableRes
    private val iconBack: Int = R.drawable.ic_ab_back,
    private val showEdit: Boolean = false,
    private val onBackClick: () -> Unit = {},
    private val onEditClick: (() -> Unit)? = null,
    private val onSettingClick: ((View) -> Unit)? = null,

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

            btSetting.show(onSettingClick != null)
            btSetting.onClick {
                onSettingClick!!(it)
            }
        }
    }
}

class MainTopBarState(
    @StringRes
    private val title: Int,
    private val onMenuClick: () -> Unit = {},
    private val onStaffListClick: () -> Unit = {},
    private val onNotificationClick: () -> Unit = {},
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarMainBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btMenu.setOnClickListener { onMenuClick() }
            btStaffList.setOnClickListener {
                onStaffListClick()
            }
            btNotification.setOnClickListener {
                onNotificationClick()
            }
        }
    }

    fun setNotificationUnreadCount(count: Int) {
        with(stateBinding) {
            tvCountNoti.show(count > 0)
            tvCountNoti.text = if (count > 100) "99+" else "$count"
        }
    }
}
