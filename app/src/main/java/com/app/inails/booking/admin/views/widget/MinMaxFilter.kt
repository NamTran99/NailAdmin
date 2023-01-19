package com.app.inails.booking.admin.views.widget

import android.text.InputFilter
import android.text.Spanned

class MinMaxFilter() : InputFilter {
        private var intMin: Float = 0f
        private var intMax: Float = 0f
  
        // Initialized
        constructor(minValue: Float, maxValue: Float) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }
  
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                if((dest.toString().toFloatOrNull() ?: 0) == intMax) return ""
                val input = (dest.toString() + source.toString()).toFloat()
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }
  
        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Float, b: Float, c: Float): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }