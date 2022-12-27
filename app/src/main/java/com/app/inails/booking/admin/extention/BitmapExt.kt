package com.app.inails.booking.admin.extention

import android.content.Context
import android.graphics.Bitmap
import com.app.inails.booking.admin.utils.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import java.io.File
import java.io.FileOutputStream

suspend fun String.scalePhotoLibrary(context: Context): File {
    val mimeType = FileUtils.getMimeType(this)
    if (mimeType!!.contains("image")) {
        val extension = this.substring(this.lastIndexOf("."))
        return if (extension == ".jpg" || extension == ".jpeg") {
            Compressor.compress(context, File(this)) {
                //        resolution(720, 1280)
                quality(60)
                format(Bitmap.CompressFormat.JPEG)
                size(2_097_152) // 2 MB
            }
        } else {
            Compressor.compress(context, File(this)) {
                //        resolution(720, 1280)
                quality(60)
                format(Bitmap.CompressFormat.WEBP)
                size(2_097_152) // 2 MB
            }
        }
    } else {
        val path = String.format(
            "%s/" + System.currentTimeMillis().toString() + ".jpeg", getTemplateFolder(
                context
            )
        )
        var bitmap = Glide.with(context)
            .asBitmap()
            .load(File(this))
            .apply(
                RequestOptions().override(500)
                    .downsample(DownsampleStrategy.CENTER_INSIDE)
                    .skipMemoryCache(true).diskCacheStrategy(
                        DiskCacheStrategy.NONE
                    )
            )
            .submit().get()

        val out = FileOutputStream(File(path))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
        out.flush()
        out.close()

        return File(path)
    }

}


fun getTemplateFolder(context: Context): String {
    val folder = String.format("%s/temp", context.cacheDir)
    File(folder).mkdirs()
    return String.format("%s/temp", context.cacheDir)
}