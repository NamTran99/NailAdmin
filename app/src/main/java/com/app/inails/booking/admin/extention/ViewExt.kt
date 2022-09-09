package com.app.inails.booking.admin.extention

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.DimenRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import java.util.*

fun View.onClick(callback: View.OnClickListener?) {
    val clickTime = 300
    setOnClickListener {
        val lastClick = getTag(R.id.tag_view_click) as? Long ?: 0L
        val now = System.currentTimeMillis()
        if (now - lastClick > clickTime) {
            callback?.onClick(it)
            setTag(R.id.tag_view_click, now)
        }
    }
}

fun RecyclerView.LayoutParams.itemMargin(position: Int, margin: Int) {
    setMargins(margin, if (position == 0) margin else 0, margin, margin)
}

fun CompoundButton.setCustomChecked(
    value: Boolean,
    listener: CompoundButton.OnCheckedChangeListener
) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}

fun EditText.focus() {
    requestFocus()
    setSelection(0, length())
}

fun View.alpha(value: Int = 170) {
    this.background.alpha = value
}

fun View.disableAlpha() {
    this.background.alpha = 255//max
}

fun List<View>.alpha() {
    this.forEach { it.alpha() }
}

fun View.setMarginTop(it: Float) {
    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = it.toInt()
}

fun View.setMarginTop(@DimenRes dimen: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = if (dimen == 0) 0 else
        resources.getDimensionPixelSize(dimen)
}

@Suppress("unchecked_cast")
fun ViewGroup.setContentView(id: Int) {
    removeAllViews()
    if (id == 0) return
    var cache = tag as? HashMap<Int, View>
    if (cache == null) {
        cache = hashMapOf()
        tag = cache
    }
    val view = if (cache.containsKey(id)) cache[id] else {
        LayoutInflater.from(context).inflate(id, this, false).also {
            cache[id] = it
        }
    }
    addView(view)
}

fun ViewGroup.of(id: Int, function: ViewGroup.() -> Unit) {
    setContentView(id)
    function()
}

fun TextView.format(vararg format: Any): String {
    return text.toString().format(Locale.getDefault(), *format)
}

fun TextView.addSpan(
    spanValue: String,
    spanned: CharacterStyle,
    textValue: String = text.toString()
) {
    val span = SpannableString(textValue)
    val start = span.indexOf(spanValue)
    if (start == -1) return
    val end = start + spanValue.length
    span.setSpan(spanned, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (spanned is ClickableSpan) {
        movementMethod = LinkMovementMethod.getInstance()
    }
    setText(span, TextView.BufferType.SPANNABLE)
}

fun ViewGroup.inflate(id: Int): View {
    return LayoutInflater.from(context).inflate(id, this, false)
}

fun Context.with(
    attrs: AttributeSet?,
    type: IntArray,
    defStyleAttr: Int,
    function: (TypedArray) -> Unit
) {
    if (attrs != null) {
        val typed = obtainStyledAttributes(attrs, type, defStyleAttr, 0)
        function(typed)
        typed.recycle()
    }
}

operator fun View.plus(view: View): List<View> {
    return arrayListOf(this, view)
}

fun List<View>.visible(b: Boolean = true, callback: () -> Unit = {}) {
    val visible = if (b) View.VISIBLE else View.INVISIBLE
    forEach { it.visibility = visible }
    if (b) callback()
}

fun <T : View> T.show(b: Boolean = true, function: T.() -> Unit = {}) {
    visibility = if (b) {
        function()
        View.VISIBLE
    } else View.GONE
}

fun <T : View> T.visible(b: Boolean = true, function: T.() -> Unit = {}) {
    visibility = if (b) {
        function()
        View.VISIBLE
    } else View.INVISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

infix fun Boolean.show(views: List<View>) {
    val visible = if (this) View.VISIBLE else View.GONE
    views.forEach { it.visibility = visible }
}

infix fun Boolean.visible(view: View) {
    view.visibility = if (this) View.VISIBLE else View.INVISIBLE
}

infix fun Boolean.show(view: View) {
    view.visibility = if (this) View.VISIBLE else View.GONE
}

fun List<View>.show(b: Boolean = true, callback: () -> Unit = {}) {
    val visible = if (b) View.VISIBLE else View.GONE
    forEach { it.visibility = visible }
    if (b) callback()
}

fun Array<View>.show(b: Boolean = true, callback: () -> Unit = {}) {
    val visible = if (b) View.VISIBLE else View.GONE
    forEach { it.visibility = visible }
    if (b) callback()
}

//fun Array<Button>.show(b: Boolean = true, callback: () -> Unit = {}) {
//    val visible = if (b) View.VISIBLE else View.GONE
//    forEach { it.visibility = visible }
//    if (b) callback()
//}

infix fun Boolean.lock(view: View) {
    view.isEnabled = !this
}

infix fun Boolean.lock(views: List<View>) {
    views.forEach { it.isEnabled = !this }
}

fun Array<View>.lock(b: Boolean = true, callback: () -> Unit = {}) {
    forEach { it.isEnabled = b }
    if (b) callback()
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Context.toPx(dp: Float): Int {
    return (dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun Context.toDp(px: Int): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun View.addShadow(
    drawable: Drawable,
    radius: Float = 4f,
    blurMaskFilter: BlurMaskFilter.Blur = BlurMaskFilter.Blur.NORMAL
) {
    val originalBitmap = drawable.toBitmap(measuredWidth, measuredHeight)
    val blurFilter = BlurMaskFilter(radius, blurMaskFilter)
    val shadowPaint = Paint()
    shadowPaint.maskFilter = blurFilter

    val offsetXY = IntArray(2)
    val shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY)

    /* Need to convert shadowImage from 8-bit to ARGB here. */
    val shadowImage32 = shadowImage.copy(Bitmap.Config.ARGB_8888, true)
    Canvas(shadowImage32)
        .drawBitmap(
            originalBitmap,
            -offsetXY[0].toFloat(),
            -offsetXY[1].toFloat(),
            null
        )
    background = BitmapDrawable(resources, shadowImage32)
}

fun List<View>.applyTo(callback: View.() -> Unit) {
    this.forEach { callback.invoke(it) }
}

fun <T> TextView.setTextAndTag(item: T, textOf: T.() -> String = { toString() }) {
    tag = item
    text = textOf(item)
}

fun Context.loadAttrs(attrs: AttributeSet?, attrType: IntArray, function: (TypedArray) -> Unit) {
    if (attrs == null) return
    val a = obtainStyledAttributes(attrs, attrType)
    function(a)
    a.recycle()
}

fun Context.getAppResourceId(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

fun AttributeSet?.load(
    view: View, attr: IntArray,
    function: TypedArray.() -> Unit
) {
    val context = view.context
    val ta = context.obtainStyledAttributes(this, attr)
    function(ta)
    ta.recycle()
}

@SuppressLint("RestrictedApi")
fun Context.getMenu(@MenuRes id: Int): Menu {
    val menu = MenuBuilder(this)
    MenuInflater(this).inflate(id, menu)
    return menu
}

fun LinearLayout.weightSum(weightSum: Float) {
    this.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        weightSum
    )
}

fun EditText.bind(any: (String) -> Unit) {
    doOnTextChanged { text, _, _, _ -> any(text?.toString().orEmpty()) }
}

fun CheckBox.bind(any: (Boolean) -> Unit) {
    setOnCheckedChangeListener { _, b -> any(b) }
}

fun View.setMargins(@DimenRes sizeRes: Int) {
    val size = resources.getDimensionPixelSize(sizeRes)
    this.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.setMargins(size, size, size, size)
    }
}
