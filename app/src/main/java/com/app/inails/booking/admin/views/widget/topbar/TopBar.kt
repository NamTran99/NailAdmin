package com.app.inails.booking.admin.views.widget.topbar

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.*
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show

data class ExtensionButton(
    var isShow: Boolean = false,
    @StringRes var content: Int = R.string.edit,
    val onclick: (() -> Unit)? = null
)

class SimpleTopBarClientState(
    @StringRes
    private val title: Int,
    @DrawableRes
    private val iconBack: Int = R.drawable.ic_ab_back,
    private val hasDivider: Boolean = true,
    private val onBackClick: () -> Unit = {}
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarSimpleClientBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            txtABTitle.setText(title)
            btnBack.setImageResource(iconBack)
            btnBack.setOnClickListener { onBackClick() }
            divider.visibility = if (hasDivider) View.VISIBLE else View.GONE
        }
    }
}

class SimpleWithMoreTopBarState(
    @StringRes
    private val title: Int,
    private val onBackClick: () -> Unit = {},
    private val onMoreClick: () -> Unit = {}
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarSimpleWithMoreBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            txtABTitle.setText(title)
            btnBack.setOnClickListener { onBackClick() }
            btnMore.setOnClickListener { onMoreClick() }
        }
    }

    fun setTitle(title: String) = with(stateBinding) {
        txtABTitle.text = title
    }

    fun getViewMore() = stateBinding.btnMore
}

class SimpleTopBarState(
    @StringRes
    private val title: Int,
    @DrawableRes
    private val iconBack: Int = R.drawable.ic_ab_back,
    private val onBackClick: () -> Unit = {},
    private val onSettingClick: ((View) -> Unit)? = null,
    private val extensionButton: ExtensionButton = ExtensionButton()
    ) : TopBarState() {
    override val stateBinding by bindingOf(TopBarSimpleBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btBack.setImageResource(iconBack)
            btBack.setOnClickListener { onBackClick() }
            btExtension.show(extensionButton.isShow)
            btExtension.setOnClickListener {
                extensionButton.onclick?.let { it1 -> it1() }
            }
            btExtension.setText(extensionButton.content)

            btSetting.show(onSettingClick != null)
            btSetting.onClick {
                onSettingClick!!(it)
            }
        }
    }
}

class StaffTopBarState(
    @StringRes
    private val title: Int,
    @DrawableRes
    private val iconBack: Int = R.drawable.ic_ab_back,
    private val onBackClick: () -> Unit = {},
    private val onNoteClick: () -> Unit = {}
) : TopBarState() {
    override val stateBinding by bindingOf(StaffTopBarSimpleBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            tvTitle.setText(title)
            btBack.setImageResource(iconBack)
            btBack.setOnClickListener { onBackClick() }
            btNote.setOnClickListener {
                onNoteClick.invoke()
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
//            btMenu.setOnClickListener { onMenuClick() }
//            btStaffList.setOnClickListener {
//                onStaffListClick()
//            }
            btNotification.setOnClickListener {
                onNotificationClick()
            }
        }
    }

    fun setTitle(  @StringRes title: Int){
        stateBinding.tvTitle.setText(title)
    }

    fun setNotificationUnreadCount(count: Int) {
        with(stateBinding) {
            tvCountNoti.show(count > 0)
            tvCountNoti.text = if (count > 100) "99+" else "$count"
        }
    }
}

class ServiceTopBarState(
    @StringRes
    private val title: Int,
    private val onMenuClick: () -> Unit = {},
    private val onChangeSalonClick: () -> Unit = {},
    private val onLogoutClick: () -> Unit = {},
    private val onNotifyClick: () -> Unit = {}
) : TopBarState() {
    override val stateBinding by bindingOf(TopBarServiceBinding::inflate)

    override fun doApply() {
        with(stateBinding) {
            txtABTitle.setText(title)
            btnMenu.setOnClickListener { onMenuClick() }
            btnChangeSalon.setOnClickListener { onChangeSalonClick() }
            btnLogoutCustomer.setOnClickListener { onLogoutClick() }
            viewNotify.setOnClickListener { onNotifyClick() }
        }
    }

    fun setTitle(name: String?) {
        if (!name.isNullOrEmpty())
            stateBinding.txtABTitle.text = name
        else stateBinding.txtABTitle.setText(R.string.title_welcome_guest)
    }

    fun showMenu(isShow: Boolean) {
        stateBinding.btnMenu.show(isShow)
    }

    fun showLogoutCustomer(isShow: Boolean) {
        with(stateBinding) {
//            btnLogoutCustomer.show(isShow)
            btnChangeSalon.show(!isShow)
        }
    }

    fun showBadge(it: Int) = with(stateBinding) {
        txtCountNotify.text = if (it > 99) "99+" else it.toString()
        txtCountNotify.show(it > 0)
    }
}
