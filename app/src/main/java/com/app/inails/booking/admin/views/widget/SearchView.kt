package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import com.app.inails.booking.admin.R
import java.util.*

class SearchView : SearchView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setIconifiedByDefault(false)
        setBackgroundResource(R.drawable.box_search_gray)
    }

    fun getText() = query.toString()

    fun setOnSearchListener(
        timeDelay: Long = TIME_DELAY,
        onSearch: (String) -> Unit
    ) {
        var timer: Timer? = null
        val handler = Handler(Looper.getMainLooper())
        setOnQueryTextListener(object : OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                timer?.cancel()
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post { onSearch(p0 ?: "") }
                    }
                }, timeDelay)

                return false
            }
        })

    }

    companion object {
        private const val TIME_DELAY: Long = 500
    }
}
