package com.app.inails.booking.admin.model.ui

interface IIntro {
    val content: String get() = ""
    val id: Int get() = 0
    val image: String get() = ""
    val stt: Int get() = 0
    val title: String get() = ""
    val fileType: FileType get() = FileType.None
    val typeName: String get() = ""
    val url: String get() = ""
    val file: String get() = ""
    val thumbNail: String get() = ""
}

enum class FileType{
    Image, Video, None
}