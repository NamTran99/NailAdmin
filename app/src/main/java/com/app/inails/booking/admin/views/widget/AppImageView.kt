package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.getLoadingCircleDrawable
import com.app.inails.booking.admin.extention.with
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
//import com.google.android.youtube.player.internal.u
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

open class AppImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var mCurrentBackground: Int = 0
    private var mBackgroundDrawableRes: Int = 0
    private var mScaleType: ScaleType = ScaleType.FIT_CENTER
        set(value){
            field = value
            scaleType = value
        }
    private var mCircle: Boolean = false
    private var mRoundCorners: Int = 0

    init {
        context.with(attrs, R.styleable.AppImageView, defStyleAttr) {
            mRoundCorners = it.getDimensionPixelSize(R.styleable.AppImageView_imgRoundCorners, 0)
            mCircle = it.getBoolean(R.styleable.AppImageView_imgCircle, false)
            val scaleType = it.getInt(
                R.styleable.AppImageView_android_scaleType,
                ScaleType.FIT_CENTER.ordinal
            )
            mScaleType = ScaleType.values()[scaleType]
            mBackgroundDrawableRes =
                it.getResourceId(R.styleable.AppImageView_android_background, 0)
            mCurrentBackground = mBackgroundDrawableRes
        }

    }

    override fun setImageURI(uri: Uri?) {
        Picasso.get().load(uri).centerInside()
            .resize(800, 600)
            .placeholder(context.getLoadingCircleDrawable())
            .into(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    open fun setImageUrl(url: String) {
        if (url.isBlank()) {
            setImageDrawable(context.getDrawable(R.drawable.ic_no_image_2))
            return
        }
        Picasso.get().load(url).centerInside()
            .resize(2048, 2048)
            .placeholder(context.getLoadingCircleDrawable())
            .into(this)
    }

    override fun setBackgroundResource(resId: Int) {
        mCurrentBackground = resId
        super.setBackgroundResource(resId)
    }
}