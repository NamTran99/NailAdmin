package com.app.inails.booking.admin.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class LinearSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        // Only add spacing to the items, not the header or footer
        if (position >= 0) {
            val layoutManager = parent.layoutManager as? LinearLayoutManager
            layoutManager?.let {
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    outRect.top = space
                    outRect.bottom = space
                } else {
                    outRect.left = space
                    outRect.right = space
                }
            }
        }
    }
}