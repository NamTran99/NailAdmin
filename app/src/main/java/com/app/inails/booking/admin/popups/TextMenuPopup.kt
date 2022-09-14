package com.app.inails.booking.admin.popups

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.support.core.view.bindingOf
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemViewTextPopupBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.popup.PopUpServiceMore
import com.app.inails.booking.admin.model.popup.PopUpStaffMore
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter


class TextMenuPopup<T>(val context: Context) : PopupWindow(context) {
    private var mMenuAdapter: Adapter
    private val mMaxWidth = context.resources.getDimensionPixelSize(R.dimen.size_200)
    private val mMaxHeight = context.resources.getDimensionPixelSize(R.dimen.size_400)
    private var mOnItemClickListener: ((T) -> Unit)? = null
    private var mOnCallBack: ((T) -> Unit)? = null
    var mItemSelected: T? = null
    var callBackFirst = false
    private var viewSelect: View? = null

    var items: ArrayList<T>?
        set(value) {
//            callBackFirst = true
            mMenuAdapter.submit(value)
            measure()
        }
        get() = mMenuAdapter.items

    init {
        contentView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mMenuAdapter = Adapter(this)
        }

        setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.drawable.picture_frame))
        isOutsideTouchable = true
    }

    fun measure() {
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        viewSelect?.post {
            if (viewSelect != null) {
                width = viewSelect!!.measuredWidth
            }
        }

//        contentView.measure(measureSpec, measureSpec)

//        width = max(contentView.measuredWidth, mMaxWidth)
//        height = min(contentView.measuredHeight, mMaxHeight)
    }

    fun showAtRight(view: View) {
        val translate = -(contentView.measuredWidth - view.measuredWidth)

        showAsDropDown(view, translate, 0)
    }

    private fun showAtLeft(view: View) {
        val xOffset = -(contentView.measuredWidth)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var measuredWidth = windowManager.defaultDisplay.width
        var measuredHeight = windowManager.defaultDisplay.height

        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)

        val yOffset = if(viewLocation[1] + view.measuredHeight + contentView.measuredHeight < measuredHeight) 0 else contentView.measuredHeight
        showAsDropDown(view, xOffset,  -yOffset)
    }

    fun showAtRight(view: View, callback: (T) -> Unit) {
        mOnItemClickListener = callback
        showAtRight(view)
    }

    fun setListener(callback: (T) -> Unit) {
        mOnCallBack = callback
    }

    fun show(view: View) {

//        showAsDropDown(view, 0, -200)
        showAtLeft(view)
    }

    fun showEnd(view: View) {
        showAtRight(view)
    }

    fun showAtCenter(view: View) {
        val translate = -(contentView.measuredWidth - view.measuredWidth) / 2
        showAsDropDown(view, translate, 0)
    }

    fun setupWithViews(
        viewClicked: View,
        viewToRender: TextView,
        gravity: Int = Gravity.START,
        rotate: Boolean = false
    ) {
//        viewClicked.setOnClickListener {
//            when (gravity) {
//                Gravity.END -> showAtRight(it)
//                Gravity.CENTER -> showAtCenter(it)
//                Gravity.START -> showAtLeft(it)
//                else -> show(it)
//            }
//            if (rotate) {
//                setOnDismissListener { it.animate().rotation(0f).start() }
//                if (it.rotation == 0f) it.animate().rotation(90f).start()
//                else dismiss()
//            } else setOnDismissListener(null)
//        }
        viewSelect = viewToRender
        if (gravity == Gravity.START)
            viewClicked.setOnClickListener(this::show)
        else
            viewClicked.setOnClickListener(this::showEnd)
        mOnItemClickListener = {
            viewToRender.text = it.toString()
            mOnCallBack?.invoke(it!!)
        }
    }

    fun setupWithView(viewClicked: View) {
        viewClicked.setOnClickListener {
            show(it)
        }
        mOnItemClickListener = {
            mOnCallBack?.invoke(it!!)
        }
    }

    fun setupWithViewLeft(viewClicked: View) {
        show(viewClicked)
        mOnItemClickListener = {
            mOnCallBack?.invoke(it!!)
        }

    }

    fun setupWithViewRight(viewClicked: View) {
        showAtRight(viewClicked)
        mOnItemClickListener = {
            mOnCallBack?.invoke(it!!)
        }
    }


    fun setSelection(pos: Int) {
        mMenuAdapter.select(pos)
    }

    inner class Adapter(view: RecyclerView) :
        SimpleRecyclerAdapter<T, ItemViewTextPopupBinding>(view) {
        var onClickItemListener: ((T) -> Unit)? = null

        override fun onCreateBinding(parent: ViewGroup): ItemViewTextPopupBinding {
            return parent.bindingOf(ItemViewTextPopupBinding::inflate)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            item: T,
            binding: ItemViewTextPopupBinding,
            adapterPosition: Int
        ) {
            binding.apply {
                root.onClick {
                    mOnItemClickListener?.invoke(item!!)
                    mItemSelected = item
                    dismiss()
                }

                root.text = item.toString()

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


interface PopupUserMoreOwner : ViewScopeOwner {
    val popup: TextMenuPopup<PopUpStaffMore>
        get() = with(viewScope) {
            getOr("popupStaff:dialog") { TextMenuPopup<PopUpStaffMore>(context) }
        }

}

interface PopupServiceMoreOwner : ViewScopeOwner {
    val popup: TextMenuPopup<PopUpServiceMore>
        get() = with(viewScope) {
            getOr("popupService:dialog") { TextMenuPopup<PopUpServiceMore>(context) }
        }

}