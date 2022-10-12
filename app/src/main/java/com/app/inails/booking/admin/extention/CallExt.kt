package com.app.inails.booking.admin.extention

import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

infix fun File.toImagePart(key: String) = createImagePart(key, this)

fun createImagePart(field: String, file: File?): MultipartBody.Part? {
    if (file == null) return null
    if (!file.exists()) return null
    val type = FileUtils.getMimeType(file.path) ?: return null
    return MultipartBody.Part.createFormData(
        field, file.name,
        file.asRequestBody(type.toMediaTypeOrNull())
    )
}

fun RequestBodyBuilder.buildMultipart(): Map<String, RequestBody> {
    val multipart = HashMap<String, RequestBody>()
    build().forEach { multipart[it.key] = createValuePart(it.value) }
    return multipart
}

private fun createValuePart(value: String): RequestBody {
    return RequestBody.create(MultipartBody.FORM, value)
}