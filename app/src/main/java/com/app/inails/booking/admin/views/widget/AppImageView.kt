package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.with
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

open class AppImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var mCurrentBackground: Int = 0
    private var mBackgroundDrawableRes: Int = 0
    private var mScaleType: ScaleType = ScaleType.FIT_CENTER
    private var mCircle: Boolean = false
    private var mRoundCorners: Int = 0

    init {
        context.with(attrs, R.styleable.AppImageView, defStyleAttr) {
            mRoundCorners = it.getDimensionPixelSize(R.styleable.AppImageView_imgRoundCorners, 0)
            mCircle = it.getBoolean(R.styleable.AppImageView_imgCircle, false)
            val scaleType = it.getInt(
                R.styleable.AppImageView_android_scaleType,
                ScaleType.CENTER_INSIDE.ordinal
            )
            mScaleType = ScaleType.values()[scaleType]
            mBackgroundDrawableRes =
                it.getResourceId(R.styleable.AppImageView_android_background, 0)
            mCurrentBackground = mBackgroundDrawableRes
        }

    }

    override fun setImageURI(uri: Uri?) {
        load {
            load(uri)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    open fun setImageUrl(url: String) {
        if (url.isBlank()){
            setImageDrawable(context.getDrawable(R.drawable.ic_no_image_2))
            return
        }
        load {
            load(url)
        }
    }

    @SuppressLint("CheckResult")
    private fun load(function: RequestBuilder<Bitmap>. () -> RequestBuilder<Bitmap>) {
        var builder = Glide.with(this).asBitmap()
        builder = function(builder)
        builder.apply {
            val transforms = arrayListOf<BitmapTransformation>()

            val scaleOp = when (mScaleType) {
                ScaleType.CENTER_INSIDE -> CenterInside()
                ScaleType.CENTER_CROP -> CenterCrop()
                else -> FitCenter()
            }
            transforms.add(scaleOp)
            if (mCircle) transforms.add(CircleCrop())
            else if (mRoundCorners != 0) transforms.add(RoundedCorners(mRoundCorners))
            transform(*transforms.toTypedArray())
        }.listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                setBackgroundResource(mBackgroundDrawableRes)
                return true
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        }).into(this)
    }

    override fun setBackgroundResource(resId: Int) {
        mCurrentBackground = resId
        super.setBackgroundResource(resId)
    }
}