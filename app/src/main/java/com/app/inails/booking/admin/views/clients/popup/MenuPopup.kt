package com.app.inails.booking.admin.views.clients.popup

import android.content.Context
import android.graphics.Color
import android.support.core.view.ViewScopeOwner
import android.support.core.view.bindingOf
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemViewTextPopupBinding
import com.app.inails.booking.admin.model.ui.client.popup.INotificationItemOption
import com.app.inails.booking.admin.model.ui.client.popup.INotificationOption
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter
import kotlinx.coroutines.launch

class MenuPopup<T>(owner: LifecycleOwner?, context: Context) : PopupWindow(context) {
    private var mMenuAdapter: Adapter
    private var mOnItemClickListener: ((T) -> Unit)? = null
    private var mOnCallBack: ((T) -> Unit)? = null
    var mItemSelected: T? = null
    var items: List<T>?
        set(value) {
            mMenuAdapter.submit(value)
            measure()
        }
        get() = mMenuAdapter.items

    init {
        contentView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mMenuAdapter = Adapter(this)
        }
        owner?.lifecycleScope?.launch {
            owner.lifecycle.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                dismiss()
            }
        }
        setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.drawable.picture_frame))
        isOutsideTouchable = true
    }

    private fun measure() {
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        contentView.measure(measureSpec, measureSpec)
        width = contentView.measuredWidth + 100
//        height = min(contentView.measuredHeight, mMaxHeight)
    }

    fun showAtRight(view: View) {
        val translate = -(contentView.measuredWidth - view.measuredWidth)
        showAsDropDown(view, translate, 0)
    }

    fun showAtRight(view: View, callback: (T) -> Unit) {
        mOnItemClickListener = callback
        showAtRight(view)
    }

    fun setListener(callback: (T) -> Unit) {
        mOnCallBack = callback
    }

    fun setListenerWithoutViews(callback: (T) -> Unit) {
        mOnItemClickListener = { callback.invoke(it!!) }
    }

    fun show(view: View) {
        showAsDropDown(view, 0, 0)
    }

    fun showAtCenter(view: View) {
        val translate = -(contentView.measuredWidth - view.measuredWidth) / 2
        showAsDropDown(view, translate, 0)
    }

    fun setupWithViews(
        viewClicked: View,
        viewToRender: TextView,
        gravity: Int = Gravity.END,
        rotate: Boolean = true
    ) {
        viewClicked.setOnClickListener {
            when (gravity) {
                Gravity.END -> showAtRight(it)
                Gravity.CENTER -> showAtCenter(it)
                else -> show(it)
            }
            if (rotate) {
                setOnDismissListener { it.animate().rotation(0f).start() }
                if (it.rotation == 0f) it.animate().rotation(90f).start()
                else dismiss()
            } else setOnDismissListener(null)
        }
        mOnItemClickListener = {
            viewToRender.text = it.toString()
            mOnCallBack?.invoke(it!!)
        }
    }

    fun setupWithViews(
        viewClicked: View,
        gravity: Int = Gravity.END,
        rotate: Boolean = false
    ) {
        viewClicked.setOnClickListener {
            when (gravity) {
                Gravity.END -> showAtRight(it)
                Gravity.CENTER -> showAtCenter(it)
                else -> show(it)
            }
            if (rotate) {
                setOnDismissListener { it.animate().rotation(0f).start() }
                if (it.rotation == 0f) it.animate().rotation(90f).start()
                else dismiss()
            } else setOnDismissListener(null)
        }
        mOnItemClickListener = {
            mOnCallBack?.invoke(it!!)
        }
    }

    fun setupWithViewsAutoClick(
        viewClicked: View,
        gravity: Int = Gravity.END
    ) {
        setupWithViews(viewClicked,gravity)
        viewClicked.performClick()
    }

    fun setSelection(pos: Int) {
        mMenuAdapter.select(pos)
    }

    inner class Adapter(view: RecyclerView) :
        SimpleRecyclerAdapter<T, ItemViewTextPopupBinding>(view) {

        override fun onCreateBinding(parent: ViewGroup): ItemViewTextPopupBinding {
            return parent.bindingOf(ItemViewTextPopupBinding::inflate)
        }

        override fun onBindHolder(
            item: T,
            binding: ItemViewTextPopupBinding,
            adapterPosition: Int
        ) {
            binding.root.apply {
                text = item.toString()
                setTextColor(if (text.contains("Delete")) Color.RED else Color.BLACK)
                setOnClickListener {
                    mOnItemClickListener?.invoke(item!!)
                    mItemSelected = item
                    dismiss()
                }
            }
        }

        fun select(pos: Int) {
            if (pos < items!!.size) {
                val item = items!![pos]
                if (item == mItemSelected) return
                mOnItemClickListener?.invoke(item)
                mItemSelected = item
            }
        }
    }
}

interface MenuPopupOwner : ViewScopeOwner {
    val notificationPopup: MenuPopup<INotificationOption>
        get() = with(viewScope) {
            getOr("notification:general:popup") { MenuPopup(owner, context) }
        }

    val notificationItemPopup: MenuPopup<INotificationItemOption>
        get() = with(viewScope) {
            getOr("notification:item:popup") { MenuPopup(owner, context) }
        }

}