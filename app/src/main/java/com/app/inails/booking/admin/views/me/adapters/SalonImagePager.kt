package com.app.inails.booking.admin.views.me.adapters

import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BasePager
import com.app.inails.booking.admin.base.PagerHolder
import com.app.inails.booking.admin.views.widget.AppImageView

class HomeBannerPager(private val view: ViewPager) : BasePager() {

    private val mViewLooper = LoopHandler {
        val next = (view.currentItem + 1) % count
        view.setCurrentItem(next, true)
    }

    var items: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
            if (count > 0) mViewLooper.start()
            else mViewLooper.stop()
        }

    init {
        view.adapter = this
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                mViewLooper.stop()
            }

            override fun onViewAttachedToWindow(v: View?) {
                if (count > 0) mViewLooper.start()
            }
        })
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) = object :
        PagerHolder<String>(container, R.layout.item_view_salon_image) {

        override fun bind(item: String) {
            super.bind(item)
            (itemView as AppImageView).setImageUrl(item)
        }
    }

    override fun getItem(position: Int) = items?.get(position)

    override fun getCount() = items?.size ?: 0

}

private class LoopHandler(val function: () -> Unit) : Handler() {
    companion object {
        private const val LOOP_TIME_OUT = 3000L
    }

    private var mRunning: Boolean = false

    private var mRunnable = object : Runnable {
        override fun run() {
            function()
            postDelayed(this, LOOP_TIME_OUT)
        }
    }

    fun start() {
        Log.d("TAG", "NamTD8: start")
        if (mRunning) return
        mRunning = true
        postDelayed(mRunnable, LOOP_TIME_OUT)
    }

    fun stop() {
        if (!mRunning) return
        mRunning = false
        removeCallbacks(mRunnable)
    }
}
