package com.app.inails.booking.admin.extention

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import androidx.core.net.toUri
import com.squareup.picasso.Transformation


/**
 *  Dùng cho picasso
 *  case load ảnh bằng picasso những ảnh portrait bị rotate 90 dộ
 *
 */
class RotateTransformation(private val rotateRotationAngle: Int) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotateRotationAngle.toFloat())
        val bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        if (bitmap != source) {
            source.recycle()
        }
        return bitmap
    }

    override fun key(): String {
        return "rotate$rotateRotationAngle"
    }
}

fun getRotateDegree(context: Context, imageURI: String): Int {
    val input = context.contentResolver.openInputStream(imageURI.toUri())
    val ei: ExifInterface =
        if (Build.VERSION.SDK_INT > 23) ExifInterface(input!!) else ExifInterface(
            imageURI
        )
    val orientation =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    var rotationDegrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
    return rotationDegrees
}